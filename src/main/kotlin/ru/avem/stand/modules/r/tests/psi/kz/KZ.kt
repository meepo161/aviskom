package ru.avem.stand.modules.r.tests.psi.kz

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.CM.DeviceID.*
import ru.avem.stand.modules.r.communication.model.devices.danfoss.Danfoss
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.owen.trm202.TRM202
import ru.avem.stand.modules.r.communication.model.devices.owen.trm202.TRM202Model
import ru.avem.stand.modules.r.communication.model.devices.satec.pm130.PM130Model

import ru.avem.stand.modules.r.tests.KSPADTest
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.*
import java.lang.Thread.sleep
import kotlin.math.abs

class KZ : KSPADTest(view = KZView::class, reportTemplate = "kz.xlsx") {
    override val name = "Определение тока и потерь КЗ"

    override val testModel = KZModel

    override fun initVars() {
        super.initVars()

        testModel.specifiedU = PreFillModel.testTypeProp.value.fields["U"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedI = PreFillModel.testTypeProp.value.fields["I"]?.value.toDoubleOrDefault(0.0)

        testModel.specifiedCos = PreFillModel.testTypeProp.value.fields["COS"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedEfficiency =
            PreFillModel.testTypeProp.value.fields["EFFICIENCY"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedPKZ = PreFillModel.testTypeProp.value.fields["P_KZ"]?.value?.toDoubleOrDefault(0.0) ?: 0.0
        testModel.specifiedP = PreFillModel.testTypeProp.value.fields["P"]?.value.toDoubleOrDefault(0.0)

        testModel.specifiedRPM = PreFillModel.testTypeProp.value.fields["RPM"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedF = PreFillModel.testTypeProp.value.fields["F"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedScheme = PreFillModel.testTypeProp.value.fields["SCHEME"]?.value ?: "λ"

        testModel.specifiedUKZ = PreFillModel.testTypeProp.value.fields["U_KZ"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedIKZ = PreFillModel.testTypeProp.value.fields["KZ_I"]?.value.toDoubleOrDefault(0.0)
    }

    override fun initView() {
        super.initView()

        runLater {
            testModel.progressProperty.value = -1.0

            testModel.measuredData.U.value = ""
            testModel.measuredData.UAB.value = ""
            testModel.measuredData.UBC.value = ""
            testModel.measuredData.UCA.value = ""

            testModel.measuredData.I.value = ""
            testModel.measuredData.IA.value = ""
            testModel.measuredData.IB.value = ""
            testModel.measuredData.IC.value = ""

            testModel.measuredData.cos.value = ""
            testModel.measuredData.P1.value = ""

            testModel.measuredData.result.value = ""
        }
    }

    override fun startPollDevices() {
        super.startPollDevices()

        startPollControlUnit()

        if (isRunning) {
            with(PAV41) {
                addCheckableDevice(this)

                CM.startPoll(this, PM130Model.I_A_REGISTER) { value ->
                    testModel.measuredIA = abs(value.toDouble() * CURRENT_STAGE_PM130)
                    testModel.measuredData.IA.value = testModel.measuredIA.autoformat()
                    testModel.measuredI = (testModel.measuredIA + testModel.measuredIB + testModel.measuredIC) / 3
                    testModel.measuredData.I.value = testModel.measuredI.autoformat()
                }
                CM.startPoll(this, PM130Model.I_B_REGISTER) { value ->
                    testModel.measuredIB = abs(value.toDouble() * CURRENT_STAGE_PM130)
                    testModel.measuredData.IB.value = testModel.measuredIB.autoformat()
                    testModel.measuredI = (testModel.measuredIA + testModel.measuredIB + testModel.measuredIC) / 3
                    testModel.measuredData.I.value = testModel.measuredI.autoformat()
                }
                CM.startPoll(this, PM130Model.I_C_REGISTER) { value ->
                    testModel.measuredIC = abs(value.toDouble() * CURRENT_STAGE_PM130)
                    testModel.measuredData.IC.value = testModel.measuredIC.autoformat()
                    testModel.measuredI = (testModel.measuredIA + testModel.measuredIB + testModel.measuredIC) / 3
                    testModel.measuredData.I.value = testModel.measuredI.autoformat()
                }

                CM.startPoll(this, PM130Model.COS_REGISTER) { value ->
                    testModel.measuredData.cos.value = value.toDouble().autoformat()
                }
                CM.startPoll(this, PM130Model.P_REGISTER) { value ->
                    testModel.measuredData.P1.value =
                        abs(value.toDouble() * CURRENT_STAGE_PM130).autoformat()
                }
                CM.startPoll(this, PM130Model.F_REGISTER) { value ->
                    testModel.measuredData.F.value = value.toDouble().autoformat()
                }
            }
        }

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
//        if (isRunning) {
//            turnOnCircuit()
//        }
//        if (isRunning) {
//            waitUntilFIToLoad()
//            startFI()
//            sleepWhileRun(3)
//        }
//        if (isRunning) {
//            selectAmperageStage()
//        }
//        if (isRunning) {
//            waiting()
//        }
//        storeTestValues()
    }

    private fun turnOnCircuit() {
        appendMessageToLog(LogTag.INFO, "Сбор схемы")
        CM.device<PR>(DD2).onIkasKM61()
        sleep(200)
//        CM.device<PR>(DD2).onMaxAmperageStage()
        
        sleep(200)
//        CM.device<PR>(DD2).fromFI()
        sleep(200)
            CM.device<PR>(DD2).onPEQV3()
        sleep(200)
//        CM.device<PR>(DD2).onMeasuringAVEM()
        sleep(200)
    }

    fun startFi() {
        if (isRunning) {
            CM.device<Danfoss>(UZ91).setObjectParams(
                volt = 100,
                perc = 100,
            )
            CM.device<Danfoss>(UZ91).startObject()
            sleepWhileRun(5)
        }
    }

    private fun selectAmperageStage() {
        appendMessageToLog(LogTag.INFO, "Подбор токовой ступени...")
        if (isRunning && testModel.measuredI < 30) {
            appendMessageToLog(LogTag.INFO, "Переключение на 30/5")
//            CM.device<PR>(DD2).on30To5AmperageStage()
//            CM.device<PR>(DD2).offMaxAmperageStage()
            
            sleepWhileRun(3)
            if (isRunning && testModel.measuredI < 4) {
                appendMessageToLog(LogTag.INFO, "Переключение на 5/5")
//                CM.device<PR>(DD2).onMinAmperageStage()
//                CM.device<PR>(DD2).off30To5AmperageStage()
                
            }
        }
    }

    private fun waiting() {
        appendMessageToLog(LogTag.INFO, "Ожидание...")
        sleepWhileRun(3, progressProperty = testModel.progressProperty)
    }

    private fun storeTestValues() {
        testModel.storedData.U.value = testModel.measuredData.U.value
        testModel.storedData.UAB.value = testModel.measuredData.UAB.value
        testModel.storedData.UBC.value = testModel.measuredData.UBC.value
        testModel.storedData.UCA.value = testModel.measuredData.UCA.value

        testModel.storedData.I.value = testModel.measuredData.I.value
        testModel.storedData.IA.value = testModel.measuredData.IA.value
        testModel.storedData.IB.value = testModel.measuredData.IB.value
        testModel.storedData.IC.value = testModel.measuredData.IC.value

        testModel.storedData.cos.value = testModel.measuredData.cos.value
        testModel.storedData.P1.value = testModel.measuredData.P1.value
        testModel.storedData.F.value = testModel.measuredData.F.value
    }

    override fun result() {
        super.result()

        if (!isSuccess) {
            testModel.measuredData.result.value = "Прервано"
            appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
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
    }

    private fun restoreTestValues() {
        testModel.measuredData.U.value = testModel.storedData.U.value
        testModel.measuredData.UAB.value = testModel.storedData.UAB.value
        testModel.measuredData.UBC.value = testModel.storedData.UBC.value
        testModel.measuredData.UCA.value = testModel.storedData.UCA.value

        testModel.measuredData.I.value = testModel.storedData.I.value
        testModel.measuredData.IA.value = testModel.storedData.IA.value
        testModel.measuredData.IB.value = testModel.storedData.IB.value
        testModel.measuredData.IC.value = testModel.storedData.IC.value

        testModel.measuredData.cos.value = testModel.storedData.cos.value
        testModel.measuredData.P1.value = testModel.storedData.P1.value
        testModel.measuredData.F.value = testModel.storedData.F.value
    }

    override fun saveProtocol() {
        reportFields["TEST_NAME_KZ"] = name

        reportFields["POWER"] = testModel.specifiedP.toString()
        reportFields["VOLTAGE_LIN"] = testModel.specifiedU.toString()
        reportFields["COS"] = testModel.specifiedCos.toString()
        reportFields["EFFICIENCY"] = testModel.specifiedEfficiency.toString()
        reportFields["AMPERAGE_PHASE"] = testModel.specifiedI.toString()
        reportFields["RPM"] = testModel.specifiedRPM.toString()
        reportFields["FREQ"] = testModel.specifiedF.toString()
        reportFields["SCHEME"] = testModel.specifiedScheme

        reportFields["U_KZ"] = testModel.measuredData.U.value
        reportFields["L1_U_KZ"] = testModel.measuredData.UAB.value
        reportFields["L2_U_KZ"] = testModel.measuredData.UBC.value
        reportFields["L3_U_KZ"] = testModel.measuredData.UCA.value
        reportFields["I_KZ"] = testModel.measuredData.I.value
        reportFields["L1_I_KZ"] = testModel.measuredData.IA.value
        reportFields["L2_I_KZ"] = testModel.measuredData.IB.value
        reportFields["L3_I_KZ"] = testModel.measuredData.IC.value
        reportFields["TOTAL_P_KZ"] = testModel.measuredData.P1.value
        reportFields["TOTAL_PF_KZ"] = testModel.measuredData.cos.value
        reportFields["FREQ_KZ"] = testModel.measuredData.F.value
        reportFields["TEMP_AMB_KZ"] = testModel.measuredData.tempAmb.value
        reportFields["TEMP_TI_KZ"] = testModel.measuredData.tempTI.value
        reportFields["RESULT_KZ"] = testModel.measuredData.result.value

        super.saveProtocol()
    }
}
