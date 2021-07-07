package ru.avem.stand.modules.r.tests.psi.hv

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.CM.DeviceID.*
import ru.avem.stand.modules.r.communication.model.devices.LatrStuckException
import ru.avem.stand.modules.r.communication.model.devices.avem.avem3.AVEM3Model
import ru.avem.stand.modules.r.communication.model.devices.avem.latr.AvemLatrModel
import ru.avem.stand.modules.r.communication.model.devices.avem.latr.LatrControllerConfiguration
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.satec.pm130.PM130Model
import ru.avem.stand.modules.r.tests.KSPADTest
import ru.avem.stand.modules.r.tests.psi.mgr.MGRModel.latrStatus
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.runLater
import java.lang.Thread.sleep
import java.util.*
import kotlin.collections.set
import kotlin.math.abs
import ru.avem.stand.modules.r.communication.model.devices.avem.latr.AvemLatrController as AvemLatrController1

class HV : KSPADTest(view = HVView::class, reportTemplate = "hv.xlsx") {
    override val name = "МПТ. Проверка прочности изоляции"

    override val testModel = HVModel

    override fun initVars() {
        super.initVars()

        testModel.specifiedU_Y_MPT = PreFillModel.testTypeProp.value.fields["U_Y_MPT"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedI_Y_MPT = PreFillModel.testTypeProp.value.fields["I_Y_MPT"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedR_IKAS_MPT =
            PreFillModel.testTypeProp.value.fields["R_IKAS_MPT"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedR_MGR_MPT = PreFillModel.testTypeProp.value.fields["R_MGR_MPT"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedU_HV_MPT = PreFillModel.testTypeProp.value.fields["U_HV_MPT"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedU_MGR_MPT = PreFillModel.testTypeProp.value.fields["U_MGR_MPT"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedI_HV_MPT = PreFillModel.testTypeProp.value.fields["I_HV_MPT"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedT_HV_MPT = PreFillModel.testTypeProp.value.fields["T_HV_MPT"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedU_Y_SG = PreFillModel.testTypeProp.value.fields["U_Y_SG"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedU_V_SG = PreFillModel.testTypeProp.value.fields["U_V_SG"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedR_IKAS_SG = PreFillModel.testTypeProp.value.fields["R_IKAS_SG"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedR_MGR_SG = PreFillModel.testTypeProp.value.fields["R_MGR_SG"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedU_HV_SG = PreFillModel.testTypeProp.value.fields["U_HV_SG"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedU_MGR_SG = PreFillModel.testTypeProp.value.fields["U_MGR_SG"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedI_HV_SG = PreFillModel.testTypeProp.value.fields["I_HV_SG"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedT_HV_SG = PreFillModel.testTypeProp.value.fields["T_HV_SG"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedIDLE_TIME = PreFillModel.testTypeProp.value.fields["IDLE_TIME"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedLOAD_TIME = PreFillModel.testTypeProp.value.fields["LOAD_TIME"]?.value.toDoubleOrDefault(0.0)

        HVModel.initData.U.set(PreFillModel.testTypeProp.value.fields["U_HV_MPT"]?.value.toDoubleOrDefault(0.0).toString())
        HVModel.initData.I.set(PreFillModel.testTypeProp.value.fields["I_HV_MPT"]?.value.toDoubleOrDefault(0.0).toString())
    }

    override fun initView() {
        super.initView()

        runLater {
            testModel.progressProperty.value = -1.0

            testModel.measuredData.U.value = ""
            testModel.measuredData.I.value = ""
            testModel.measuredData.F.value = ""

            testModel.measuredData.result.value = ""
        }

        appendOneMessageToLog(LogTag.ERROR, "Подключите высоковольтные крокодилы к ОИ МПТ!")
    }

    override fun startPollDevices() {
        super.startPollDevices()
        startPollControlUnit()

        if (isRunning) {
            with(PV25) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    if (value.toDouble() < 0) {
                        testModel.measuredData.U.value = "0.0"
                    } else {
                        testModel.measuredData.U.value = abs(value.toDouble() * COEF_TR_AVEM).autoformat()
                    }
                    testModel.measuredU = testModel.measuredData.U.value.toDoubleOrDefault(0.0)
                }
            }
        }

        if (isRunning) {
            with(PAV41) {
                addCheckableDevice(this)
                CM.startPoll(this, PM130Model.I_A_REGISTER) { value ->
                    testModel.measuredData.I.value =
                        "%.2f".format(Locale.ENGLISH, value.toDouble()/* * CURRENT_STAGE_PM130_VIU*/)
                    testModel.measuredI = testModel.measuredData.I.value.toDoubleOrDefault(0.0)
                    if (testModel.measuredI > testModel.specifiedI_HV_MPT) {
                        cause = "Ток утечки превысил заданный"
                        CM.device<PR>(DD2).offPEQV3()
                        CM.device<PR>(DD2).offVIUQV1()
                    }
                }
                CM.startPoll(this, PM130Model.F_REGISTER) { value ->
                    testModel.measuredData.F.value = value.autoformat()
                }
            }
        }
        if (isRunning) {
            with(GV240) {
                addCheckableDevice(this)
                CM.startPoll(this, AvemLatrModel.DEVICE_STATUS) { value ->
                    latrStatus = value.toInt()
                    when (latrStatus) {
                        0x81 -> {
//                            appendMessageToLog(LogTag.ERROR, "Сработал верхний концевик при движении вверх")
                        }
                        0x82 -> {
//                            appendMessageToLog(LogTag.ERROR, "Сработал нижний концевик при движении вниз")
                        }
                        0x83 -> {
                            cause = "Сработали оба концевика"
                        }
                        0x84 -> {
                            cause = "Время регулирования превысило заданное"
                        }
                        0x85 -> {
//                            cause = "Застревание АРН"
                        }
                    }
                }
            }
        }
    }

    override fun logic() {
        if (isRunning) {
            turnOnCircuit()
        }
        if (isRunning) {
            resetLatr(CM.device(GV240))
            configLatr(CM.device(GV240))
        }

        if (isRunning && testModel.measuredU < 10) {
            cause = "Крокодилы закорочены"
            CM.device<PR>(DD2).offPEQV3()
            CM.device<PR>(DD2).offVIUQV1()
        }

        if (isRunning) {
            regulateVoltage(CM.device(GV240))
        }
        if (isRunning) {
            accurateRegulate(CM.device(GV240))
        }
        if (isRunning) {
            waiting()
        }
        storeTestValues()

        resetLatr(CM.device(GV240))
        while (testModel.measuredU > 50) {
            sleep(100)
        }
        CM.device<PR>(DD2).offPEQV3()
        sleep(200)
        CM.device<PR>(DD2).offVIUQV1()
        sleep(200)
        //TODO Проверить заземление
    }

    private fun turnOnCircuit() {
        appendMessageToLog(LogTag.INFO, "Сбор схемы")
        CM.device<PR>(DD2).onShuntirGB30()
        sleep(200)
        CM.device<PR>(DD2).onPEQV3()
        sleep(200)

        //TODO Проверить заземление

        CM.device<PR>(DD2).onVIUQV1()
        sleep(200)
        CM.device<PR>(DD2).offShuntirGB30()
        sleep(200)
        CM.device<PR>(DD2).onViuArnKM30()
        sleep(200)
        CM.device<PR>(DD2).onOtvodK30()
        sleep(200)
    }

    private fun resetLatr(latrDevice: AvemLatrController1) {
        CM.device<AvemLatrController1>(GV240).stop()
        try {
            latrDevice.reset({}, GV240)
        } catch (e: LatrStuckException) {
            appendMessageToLog(LogTag.ERROR, "Ошибка возврата АРН в начало. АРН застрял.")
        }
    }

    private fun configLatr(latrDevice: AvemLatrController1) {
        appendMessageToLog(LogTag.INFO, "Конфигурирование и запуск АРН")
        latrDevice.presetParameters(
            LatrControllerConfiguration(
                minDuttyPercent = 85f,
                maxDuttyPercent = 85f,
                corridor = 0.2f,
                delta = 0.03f,
                timePulseMin = 100,
                timePulseMax = 100
            )
        )
    }

    private fun regulateVoltage(latrDevice: AvemLatrController1) {
        appendMessageToLog(LogTag.INFO, "Грубое регулирование напряжения...")
        while (isRunning && testModel.measuredU < testModel.specifiedU_HV_MPT - 300) {
            latrDevice.start((220f))
            sleep(10)
        }
    }

    private fun accurateRegulate(latrDevice: AvemLatrController1) {
        if (isRunning) {
            latrDevice.presetParameters(
                LatrControllerConfiguration(
                    minDuttyPercent = 50f,
                    maxDuttyPercent = 50f,
                    corridor = 0.2f,
                    delta = 0.03f,
                    timePulseMin = 100,
                    timePulseMax = 100
                )
            )
            appendMessageToLog(LogTag.INFO, "Точное регулирование напряжения...")
            while (isRunning &&
                (testModel.measuredU <= testModel.specifiedU_HV_MPT ||
                        testModel.measuredU >= testModel.specifiedU_HV_MPT * 1.03f)
            ) {
                if (testModel.measuredU <= testModel.specifiedU_HV_MPT) {
                    latrDevice.plusVoltage()
                    sleep(200)
                }
                if (testModel.measuredU >= testModel.specifiedU_HV_MPT * 1.03f) {
                    latrDevice.minusVoltage()
                    sleep(200)
                }
                latrDevice.stop()
                sleep(200)
            }
        }
        latrDevice.stop()
    }

    private fun waiting() {
        appendMessageToLog(LogTag.INFO, "Ожидание...")
        sleepWhileRun(testModel.specifiedT_HV_MPT.toInt(), progressProperty = testModel.progressProperty)
    }

    private fun storeTestValues() {
        testModel.storedData.U.value = testModel.measuredData.U.value
        testModel.storedData.I.value = testModel.measuredData.I.value
        testModel.storedData.F.value = testModel.measuredData.F.value
    }

    override fun result() {
        super.result()

        if (!isSuccess) {
            if (cause == "ток утечки превысил заданный") {
                testModel.measuredData.result.value = "Пробой"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
            } else {
                testModel.measuredData.result.value = "Прервано"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
            }
        } else {
            testModel.measuredData.result.value = "Соответствует"
            appendMessageToLog(LogTag.INFO, "Испытание завершено успешно")
        }
    }

    override fun finalizeView() {
        super.finalizeView()
        restoreTestValues()
        runLater {
            testModel.progressProperty.value = 0.0
        }
        appendOneMessageToLog(LogTag.ERROR, "Отключите высоковольтные крокодилы")
    }

    private fun restoreTestValues() {
        testModel.measuredData.U.value = testModel.storedData.U.value
        testModel.measuredData.I.value = testModel.storedData.I.value
        testModel.measuredData.time.value = testModel.storedData.time.value
    }

    override fun saveProtocol() {
        reportFields["TEST_NAME_HV"] = name

        reportFields["U_MEAS_HV_MPT"] = testModel.measuredData.U.value
        reportFields["I_MEAS_HV_MPT"] = testModel.measuredData.I.value
        reportFields["TIME_MEAS_HV_MPT"] = testModel.measuredData.time.value
        reportFields["RESULT_HV_MPT"] = testModel.measuredData.result.value

        super.saveProtocol()
    }
}
