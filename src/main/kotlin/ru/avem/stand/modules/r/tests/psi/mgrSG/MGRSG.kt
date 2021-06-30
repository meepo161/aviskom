package ru.avem.stand.modules.r.tests.psi.mgrSG

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.CM.DeviceID.*
import ru.avem.stand.modules.r.communication.model.devices.megaohmmeter.cs02021.CS02021
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.owen.trm202.TRM202
import ru.avem.stand.modules.r.communication.model.devices.owen.trm202.TRM202Model
import ru.avem.stand.modules.r.tests.KSPADTest
import ru.avem.stand.modules.r.tests.psi.mgr.MGRModel
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.*
import java.lang.Thread.sleep

class MGRSG : KSPADTest(view = MGRViewSG::class, reportTemplate = "mgr.xlsx") {
    override val name = "СГ. Измерение сопротивления изоляции обмоток и встроенных термодатчиков относительно корпуса и " +
            "между обмотками в практически холодном состоянии"
    override val testModel = MGRModelSG

    override fun initVars() {
        super.initVars()

        testModel.specifiedU_Y_MPT =    PreFillModel.testTypeProp.value.fields["U_Y_MPT"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedI_Y_MPT =    PreFillModel.testTypeProp.value.fields["I_Y_MPT"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedR_IKAS_MPT = PreFillModel.testTypeProp.value.fields["R_IKAS_MPT"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedR_MGR_MPT =  PreFillModel.testTypeProp.value.fields["R_MGR_MPT"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedU_HV_MPT =   PreFillModel.testTypeProp.value.fields["U_HV_MPT"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedU_MGR_MPT =  PreFillModel.testTypeProp.value.fields["U_MGR_MPT"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedI_HV_MPT =   PreFillModel.testTypeProp.value.fields["I_HV_MPT"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedT_HV_MPT =   PreFillModel.testTypeProp.value.fields["T_HV_MPT"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedU_Y_SG =     PreFillModel.testTypeProp.value.fields["U_Y_SG"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedU_V_SG =     PreFillModel.testTypeProp.value.fields["U_V_SG"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedR_IKAS_SG =  PreFillModel.testTypeProp.value.fields["R_IKAS_SG"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedR_MGR_SG =   PreFillModel.testTypeProp.value.fields["R_MGR_SG"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedU_HV_SG =    PreFillModel.testTypeProp.value.fields["U_HV_SG"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedU_MGR_SG =   PreFillModel.testTypeProp.value.fields["U_MGR_SG"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedI_HV_SG =    PreFillModel.testTypeProp.value.fields["I_HV_SG"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedT_HV_SG =    PreFillModel.testTypeProp.value.fields["T_HV_SG"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedIDLE_TIME =  PreFillModel.testTypeProp.value.fields["IDLE_TIME"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedLOAD_TIME =  PreFillModel.testTypeProp.value.fields["LOAD_TIME"]?.value.toDoubleOrDefault(0.0)
    }

    override fun initView() {
        super.initView()

        runLater {
            testModel.progressProperty.value = -1.0

            testModel.measuredData.U.value = ""
            testModel.measuredData.R15.value = ""
            testModel.measuredData.R60.value = ""
            testModel.measuredData.K_ABS.value = ""
            testModel.measuredData.tempAmb.value = ""
            testModel.measuredData.tempTI.value = ""
            testModel.measuredData.result.value = ""
        }
    }

    override fun startPollDevices() {
        super.startPollDevices()
        startPollControlUnit()

        if (isRunning) {
            with(CM.device<TRM202>(PS81)) {
                with(getRegisterById(TRM202Model.T_1)) {
                    readRegister(this)
                    testModel.measuredData.tempAmb.value = value.toDouble().autoformat()
                }
                with(getRegisterById(TRM202Model.T_2)) {
                    readRegister(this)
                    testModel.measuredData.tempTI.value = value.toDouble().autoformat()
                }
            }
        }
    }

    override fun logic() {
        if (isRunning) {
            turnOnCircuit()
        }
        if (isRunning) {
            startMeasuring()
        }
    }

    private fun turnOnCircuit() {
        appendMessageToLog(LogTag.INFO, "Сбор схемы")
        CM.device<PR>(DD2).onShuntirGB30()
        sleep(200)
        CM.device<PR>(DD2).onPEQV3()
        sleep(200)

        // TODO проверка разомкнулась ли земля

        CM.device<PR>(DD2).onMGRQV2()
        sleep(200)
        CM.device<PR>(DD2).offShuntirGB30()
    }

    private fun startMeasuring() {
        appendMessageToLog(LogTag.INFO, "Начало измерения...")

        with(CM.device<CS02021>(PR65)) {
            if (isResponding) {
                setVoltage(testModel.specifiedR_MGR_SG.toInt())
                sleepWhileRun(90, progressProperty = testModel.progressProperty)
                val measuredR60 = readData()[0].toDouble()
                val measuredUr = readData()[1].toDouble()
                val measuredAbs = readData()[2].toDouble()
                val measuredR15 = readData()[3].toDouble()

                val measuredR60Mohm = (measuredR60 / 1_000_000)
                val measuredR15Mohm = (measuredR15 / 1_000_000)
                if (measuredR60Mohm > 200_000) {
                    testModel.measuredData.U.value = measuredUr.autoformat()
                    testModel.measuredData.R15.value = "обрыв"
                    testModel.measuredData.R60.value = "обрыв"
                    testModel.measuredData.K_ABS.value = "обрыв"
                    cause = "обрыв"
                } else {
                    testModel.measuredData.U.value = measuredUr.autoformat()
                    testModel.measuredData.R15.value = measuredR15Mohm.autoformat()
                    testModel.measuredData.R60.value = measuredR60Mohm.autoformat()
                    testModel.measuredData.K_ABS.value = measuredAbs.autoformat()
                    CM.device<PR>(DD2).offPEQV3()
                    CM.device<PR>(DD2).offMGRQV2()
                    //TODO проверить заземление
                    CM.device<PR>(DD2).offShuntirGB30()
                    appendMessageToLog(LogTag.DEBUG, "Заземление")
                    sleepWhileRun(30, progressProperty = testModel.progressProperty)
                }

            } else {
                cause = "Меггер не отвечает"
            }
        }
    }

    override fun result() {
        super.result()

        when {
            !isSuccess -> {
                testModel.measuredData.result.value = "Прервано"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
            }
//            testModel.measuredData.R60.value.toDouble() < testModel.specifiedData.R60.value.toDouble() -> { TODO
//                testModel.measuredData.result.value = "Не соответствует"
//                appendMessageToLog(LogTag.ERROR, "Измеренное сопротивление < ${testModel.specifiedData.R60.value} МОм")
//            }
            testModel.measuredData.K_ABS.value.toDouble() < 1.3 -> {
                testModel.measuredData.result.value = "Не соответствует"
                appendMessageToLog(LogTag.ERROR, "Измеренный kABS < 1.3")
            }
            else -> {
                testModel.measuredData.result.value = "Соответствует"
                appendMessageToLog(LogTag.INFO, "Испытание завершено успешно")
            }
        }
    }

    override fun finalizeView() {
        super.finalizeView()
        runLater {
            testModel.progressProperty.value = 0.0
        }
    }

    override fun saveProtocol() {
        reportFields["TEST_NAME_MGR"] = name

        reportFields["U_SPEC_MGR_SG"] = testModel.specifiedData.U.value
        reportFields["R_SPEC_MGR_SG"] = testModel.specifiedData.R60.value
        reportFields["U_MEAS_MGR_SG"] = testModel.measuredData.U.value
        reportFields["R15_MEAS_MGR_SG"] = testModel.measuredData.R15.value
        reportFields["R60_MEAS_MGR_SG"] = testModel.measuredData.R60.value
        reportFields["K_ABS_MEAS_MGR_SG"] = testModel.measuredData.K_ABS.value
        reportFields["TEMP_AMB_MGR_SG"] = testModel.measuredData.tempAmb.value
        reportFields["TEMP_TI_MGR_SG"] = testModel.measuredData.tempTI.value
        reportFields["RESULT_MGR_SG"] = testModel.measuredData.result.value

        super.saveProtocol()
    }
}
