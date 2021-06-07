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

    override val testModel = MGRModel

    override fun initVars() {
        super.initVars()

        MGRModel.specifiedU = PreFillModel.testTypeProp.value.fields["U"]?.value.toDoubleOrDefault(0.0)
        MGRModel.specifiedI = PreFillModel.testTypeProp.value.fields["I"]?.value.toDoubleOrDefault(0.0)

        MGRModel.specifiedCos = PreFillModel.testTypeProp.value.fields["COS"]?.value.toDoubleOrDefault(0.0)
        MGRModel.specifiedEfficiency =
            PreFillModel.testTypeProp.value.fields["EFFICIENCY"]?.value.toDoubleOrDefault(0.0)
        MGRModel.specifiedP = PreFillModel.testTypeProp.value.fields["P"]?.value.toDoubleOrDefault(0.0)

        MGRModel.specifiedRPM = PreFillModel.testTypeProp.value.fields["RPM"]?.value.toDoubleOrDefault(0.0)
        MGRModel.specifiedF = PreFillModel.testTypeProp.value.fields["F"]?.value.toDoubleOrDefault(0.0)
        MGRModel.specifiedScheme = PreFillModel.testTypeProp.value.fields["SCHEME"]?.value ?: "λ"

        MGRModel.specifiedUMGR = PreFillModel.testTypeProp.value.fields["U_MGR"]?.value.toDoubleOrDefault(0.0)
        MGRModel.specifiedRMGR = PreFillModel.testTypeProp.value.fields["R_MGR_HV"]?.value.toDoubleOrDefault(0.0)
    }

    override fun initView() {
        super.initView()

        runLater {
            testModel.progressProperty.value = -1.0

            MGRModel.measuredData.U.value = ""
            MGRModel.measuredData.R15.value = ""
            MGRModel.measuredData.R60.value = ""
            MGRModel.measuredData.K_ABS.value = ""
            MGRModel.measuredData.tempAmb.value = ""
            MGRModel.measuredData.tempTI.value = ""
            MGRModel.measuredData.result.value = ""
        }
    }

    override fun startPollDevices() {
        super.startPollDevices()
        startPollControlUnit()

        if (isRunning) {
            with(CM.device<TRM202>(PS81)) {
                with(getRegisterById(TRM202Model.T_1)) {
                    readRegister(this)
                    MGRModel.measuredData.tempAmb.value = value.toDouble().autoformat()
                }
                with(getRegisterById(TRM202Model.T_2)) {
                    readRegister(this)
                    MGRModel.measuredData.tempTI.value = value.toDouble().autoformat()
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
                setVoltage(MGRModel.specifiedUMGR.toInt())
                sleepWhileRun(90, progressProperty = testModel.progressProperty)
                val measuredR60 = readData()[0].toDouble()
                val measuredUr = readData()[1].toDouble()
                val measuredAbs = readData()[2].toDouble()
                val measuredR15 = readData()[3].toDouble()

                val measuredR60Mohm = (measuredR60 / 1_000_000)
                val measuredR15Mohm = (measuredR15 / 1_000_000)
                if (measuredR60Mohm > 200_000) {
                    MGRModel.measuredData.U.value = measuredUr.autoformat()
                    MGRModel.measuredData.R15.value = "обрыв"
                    MGRModel.measuredData.R60.value = "обрыв"
                    MGRModel.measuredData.K_ABS.value = "обрыв"
                    cause = "обрыв"
                } else {
                    MGRModel.measuredData.U.value = measuredUr.autoformat()
                    MGRModel.measuredData.R15.value = measuredR15Mohm.autoformat()
                    MGRModel.measuredData.R60.value = measuredR60Mohm.autoformat()
                    MGRModel.measuredData.K_ABS.value = measuredAbs.autoformat()
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
                MGRModel.measuredData.result.value = "Прервано"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
            }
//            testModel.measuredData.R60.value.toDouble() < testModel.specifiedData.R60.value.toDouble() -> { TODO
//                testModel.measuredData.result.value = "Не соответствует"
//                appendMessageToLog(LogTag.ERROR, "Измеренное сопротивление < ${testModel.specifiedData.R60.value} МОм")
//            }
            MGRModel.measuredData.K_ABS.value.toDouble() < 1.3 -> {
                MGRModel.measuredData.result.value = "Не соответствует"
                appendMessageToLog(LogTag.ERROR, "Измеренный kABS < 1.3")
            }
            else -> {
                MGRModel.measuredData.result.value = "Соответствует"
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

        reportFields["POWER"] = MGRModel.specifiedP.toString()
        reportFields["VOLTAGE_LIN"] = MGRModel.specifiedU.toString()
        reportFields["COS"] = MGRModel.specifiedCos.toString()
        reportFields["EFFICIENCY"] = MGRModel.specifiedEfficiency.toString()
        reportFields["AMPERAGE_PHASE"] = MGRModel.specifiedI.toString()
        reportFields["RPM"] = MGRModel.specifiedRPM.toString()
        reportFields["FREQ"] = MGRModel.specifiedF.toString()
        reportFields["SCHEME"] = MGRModel.specifiedScheme

        reportFields["U_SPEC_MGR"] = MGRModel.specifiedData.U.value
        reportFields["R_SPEC_MGR"] = MGRModel.specifiedData.R60.value
        reportFields["U_MEAS_MGR"] = MGRModel.measuredData.U.value
        reportFields["R15_MEAS_MGR"] = MGRModel.measuredData.R15.value
        reportFields["R60_MEAS_MGR"] = MGRModel.measuredData.R60.value
        reportFields["K_ABS_MEAS_MGR"] = MGRModel.measuredData.K_ABS.value
        reportFields["TEMP_AMB_MGR"] = MGRModel.measuredData.tempAmb.value
        reportFields["TEMP_TI_MGR"] = MGRModel.measuredData.tempTI.value
        reportFields["RESULT_MGR"] = MGRModel.measuredData.result.value

        super.saveProtocol()
    }
}
