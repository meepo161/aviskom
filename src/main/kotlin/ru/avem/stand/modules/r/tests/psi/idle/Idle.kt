package ru.avem.stand.modules.r.tests.psi.idle

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.CM.DeviceID.*
import ru.avem.stand.modules.r.communication.model.devices.avem.avem3.AVEM3Model
import ru.avem.stand.modules.r.communication.model.devices.delta.c2000.C2000
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.owen.trm202.TRM202Model
import ru.avem.stand.modules.r.communication.model.devices.satec.pm130.PM130Model
import ru.avem.stand.modules.r.tests.KSPADTest
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.runLater
import java.lang.Thread.sleep
import kotlin.collections.set
import kotlin.math.abs

class Idle : KSPADTest(view = IdleView::class, reportTemplate = "idle.xlsx") {
    override val name = "Измерение потерь в ХХ.Правильность чередования фаз"

    override val testModel = IdleModel

    override fun initVars() {
        super.initVars()

        testModel.specifiedU = PreFillModel.testTypeProp.value.fields["U"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedI = PreFillModel.testTypeProp.value.fields["I"]?.value.toDoubleOrDefault(0.0)

        testModel.specifiedCos = PreFillModel.testTypeProp.value.fields["COS"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedEfficiency =
            PreFillModel.testTypeProp.value.fields["EFFICIENCY"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedP = PreFillModel.testTypeProp.value.fields["P"]?.value.toDoubleOrDefault(0.0)

        testModel.specifiedRPM = PreFillModel.testTypeProp.value.fields["RPM"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedF = PreFillModel.testTypeProp.value.fields["F"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedScheme = PreFillModel.testTypeProp.value.fields["SCHEME"]?.value ?: "λ"

        testModel.specifiedIdleI =
            PreFillModel.testTypeProp.value.fields["IDLE_I"]?.value?.toDoubleOrDefault(0.0) ?: 0.0
        testModel.specifiedIdleTestTime =
            PreFillModel.testTypeProp.value.fields["IDLE_TIME"]?.value.toDoubleOrDefault(0.0)
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

            testModel.measuredData.P1.value = ""
            testModel.measuredData.F.value = ""
            testModel.measuredData.cos.value = ""

            testModel.measuredData.v1x.value = ""
            testModel.measuredData.v1y.value = ""
            testModel.measuredData.v1z.value = ""

            testModel.measuredData.v2x.value = ""
            testModel.measuredData.v2y.value = ""
            testModel.measuredData.v2z.value = ""

            testModel.measuredData.result.value = ""
        }
    }

    override fun startPollDevices() {
        super.startPollDevices()

        startPollControlUnit()

        if (isRunning) {
            with(PAV41) {
                addCheckableDevice(this)
                CM.startPoll(this, PM130Model.U_AB_REGISTER) { value ->
                    testModel.measuredData.UAB.value = value.toDouble().autoformat()
                    testModel.measuredU =
                        (testModel.measuredData.UAB.value.toDoubleOrDefault(0.0)
                                + testModel.measuredData.UBC.value.toDoubleOrDefault(0.0)
                                + testModel.measuredData.UCA.value.toDoubleOrDefault(0.0)) / 3.0
                    testModel.measuredData.U.value = testModel.measuredU.autoformat()
                }
                CM.startPoll(this, PM130Model.U_BC_REGISTER) { value ->
                    testModel.measuredData.UBC.value = value.toDouble().autoformat()
                    testModel.measuredU =
                        (testModel.measuredData.UAB.value.toDoubleOrDefault(0.0)
                                + testModel.measuredData.UBC.value.toDoubleOrDefault(0.0)
                                + testModel.measuredData.UCA.value.toDoubleOrDefault(0.0)) / 3.0
                    testModel.measuredData.U.value = testModel.measuredU.autoformat()
                }
                CM.startPoll(this, PM130Model.U_CA_REGISTER) { value ->
                    testModel.measuredData.UCA.value = value.toDouble().autoformat()
                    testModel.measuredU =
                        (testModel.measuredData.UAB.value.toDoubleOrDefault(0.0)
                                + testModel.measuredData.UBC.value.toDoubleOrDefault(0.0)
                                + testModel.measuredData.UCA.value.toDoubleOrDefault(0.0)) / 3.0
                    testModel.measuredData.U.value = testModel.measuredU.autoformat()
                }

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
            with(PV23) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredUA = value.toDouble()
                    testModel.measuredData.UA.value = testModel.measuredUA.autoformat()
                }
            }
        }

        if (isRunning) {
            with(PV25) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredUB = value.toDouble()
                    testModel.measuredData.UB.value = testModel.measuredUB.autoformat()
                }
            }
        }

        if (isRunning) {
            with(PV24) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredIA = value.toDouble() * COEF_SHUNT
                    testModel.measuredData.IA.value = testModel.measuredIA.autoformat()
                }
            }
        }

        if (isRunning) {
            with(PV27) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredIB = value.toDouble() * COEF_SHUNT
                    testModel.measuredData.IB.value = testModel.measuredIB.autoformat()
                }
            }
        }

        if (isRunning) {
            with(PS81) {
                addCheckableDevice(this)
                CM.startPoll(this, TRM202Model.T_1) { value ->
                    testModel.measuredData.tempAmb.value = value.toDouble().autoformat()
                }
                CM.startPoll(this, TRM202Model.T_2) { value ->
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
            waitUntilFIToLoad()
            startFI()
            waitUntilFIToRun()
        }
        if (isRunning) {
            waiting()
        }
        storeTestValues()
        if (isRunning) {
            stopFI(CM.device(UZ91))
        }
    }

    private fun turnOnCircuit() {
        appendMessageToLog(LogTag.INFO, "Сбор схемы")
        CM.device<PR>(DD2).onIkasKM61()
        sleep(200)
    }

    private fun startFI() {
        appendMessageToLog(LogTag.INFO, "Разгон ЧП...")
        CM.device<C2000>(UZ91).setObjectParams(
            fOut = testModel.specifiedF,

            voltageP1 = testModel.specifiedU,
            fP1 = testModel.specifiedF,

            voltageP2 = 1,
            fP2 = 1
        )
        CM.device<C2000>(UZ91).startObject()
    }

    private fun waiting() {
        appendMessageToLog(LogTag.INFO, "Ожидание...")
        sleepWhileRun(testModel.specifiedIdleTestTime.toInt(), progressProperty = testModel.progressProperty)
    }

    private fun storeTestValues() {
        testModel.storedData.U.value =
            testModel.measuredData.U.value // TODO проверить здесь и в остальных местах всё ли я сохраняю
        testModel.storedData.UAB.value = testModel.measuredData.UAB.value
        testModel.storedData.UBC.value = testModel.measuredData.UBC.value
        testModel.storedData.UCA.value = testModel.measuredData.UCA.value

        testModel.storedData.I.value = testModel.measuredData.I.value
        testModel.storedData.IA.value = testModel.measuredData.IA.value
        testModel.storedData.IB.value = testModel.measuredData.IB.value
        testModel.storedData.IC.value = testModel.measuredData.IC.value

        testModel.storedData.P1.value = testModel.measuredData.P1.value
        testModel.storedData.F.value = testModel.measuredData.F.value
        testModel.storedData.cos.value = testModel.measuredData.cos.value

        testModel.storedData.v1.value = testModel.measuredData.v1.value
        testModel.storedData.v1x.value = testModel.measuredData.v1x.value
        testModel.storedData.v1y.value = testModel.measuredData.v1y.value
        testModel.storedData.v1z.value = testModel.measuredData.v1z.value

        testModel.storedData.v2.value = testModel.measuredData.v2.value
        testModel.storedData.v2x.value = testModel.measuredData.v2x.value
        testModel.storedData.v2y.value = testModel.measuredData.v2y.value
        testModel.storedData.v2z.value = testModel.measuredData.v2z.value
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

        testModel.measuredData.P1.value = testModel.storedData.P1.value
        testModel.measuredData.F.value = testModel.storedData.F.value
        testModel.measuredData.cos.value = testModel.storedData.cos.value

        testModel.measuredData.v1.value = testModel.storedData.v1.value
        testModel.measuredData.v1x.value = testModel.storedData.v1x.value
        testModel.measuredData.v1y.value = testModel.storedData.v1y.value
        testModel.measuredData.v1z.value = testModel.storedData.v1z.value

        testModel.measuredData.v2.value = testModel.storedData.v2.value
        testModel.measuredData.v2x.value = testModel.storedData.v2x.value
        testModel.measuredData.v2y.value = testModel.storedData.v2y.value
        testModel.measuredData.v2z.value = testModel.storedData.v2z.value
    }

    override fun saveProtocol() {
        reportFields["TEST_NAME_IDLE"] = name

        reportFields["POWER"] = testModel.specifiedP.toString()
        reportFields["VOLTAGE_LIN"] = testModel.specifiedU.toString()
        reportFields["COS"] = testModel.specifiedCos.toString()
        reportFields["EFFICIENCY"] = testModel.specifiedEfficiency.toString()
        reportFields["AMPERAGE_PHASE"] = testModel.specifiedI.toString()
        reportFields["RPM"] = testModel.specifiedRPM.toString()
        reportFields["FREQ"] = testModel.specifiedF.toString()
        reportFields["SCHEME"] = testModel.specifiedScheme

        reportFields["U_IDLE"] = testModel.measuredData.U.value
        reportFields["L1_U_IDLE"] = testModel.measuredData.UAB.value
        reportFields["L2_U_IDLE"] = testModel.measuredData.UBC.value
        reportFields["L3_U_IDLE"] = testModel.measuredData.UCA.value
        reportFields["I_IDLE"] = testModel.measuredData.I.value
        reportFields["L1_I_IDLE"] = testModel.measuredData.IA.value
        reportFields["L2_I_IDLE"] = testModel.measuredData.IB.value
        reportFields["L3_I_IDLE"] = testModel.measuredData.IC.value
        reportFields["TOTAL_P_IDLE"] = testModel.measuredData.P1.value
        reportFields["TOTAL_PF_IDLE"] = testModel.measuredData.cos.value
        reportFields["F_IDLE"] = testModel.measuredData.F.value
        reportFields["X_31_IDLE"] = testModel.measuredData.v1x.value
        reportFields["Y_31_IDLE"] = testModel.measuredData.v1y.value
        reportFields["Z_31_IDLE"] = testModel.measuredData.v1z.value
        reportFields["X_32_IDLE"] = testModel.measuredData.v2x.value
        reportFields["Y_32_IDLE"] = testModel.measuredData.v2y.value
        reportFields["Z_32_IDLE"] = testModel.measuredData.v2z.value
        reportFields["TEMP_IDLE"] = testModel.measuredData.tempAmb.value
        reportFields["RESULT_IDLE"] = testModel.measuredData.result.value

        super.saveProtocol()
    }
}
