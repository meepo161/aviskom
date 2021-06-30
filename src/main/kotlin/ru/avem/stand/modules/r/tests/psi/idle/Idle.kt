package ru.avem.stand.modules.r.tests.psi.idle

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.CM.DeviceID.*
import ru.avem.stand.modules.r.communication.model.devices.avem.avem3.AVEM3Model
import ru.avem.stand.modules.r.communication.model.devices.danfoss.Danfoss
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.owen.trm202.TRM202Model
import ru.avem.stand.modules.r.tests.KSPADTest
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.runLater
import java.lang.Thread.sleep
import kotlin.collections.set

class Idle : KSPADTest(view = IdleView::class, reportTemplate = "idle.xlsx") {
    override val name = "Измерение потерь в ХХ"

    override val testModel = IdleModel

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
            testModel.measuredData.I_Y_MPT.value = ""
            testModel.measuredData.I_V_MPT.value = ""
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
            with(PV23) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredUA = value.toDouble()
                    testModel.measuredData.U_Y_MPT.value = testModel.measuredUA.autoformat()
                }
            }
        }
// 10% = 0,02
        if (isRunning) {
            with(PV25) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredUB = value.toDouble()
                    testModel.measuredData.U_V_MPT.value = testModel.measuredUB.autoformat()
                }
            }
        }

        if (isRunning) {
            with(PV24) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredIA = value.toDouble() * COEF_SHUNT_PV24
                    testModel.measuredData.I_Y_MPT.value = testModel.measuredIA.autoformat()
                }
            }
        }

        if (isRunning) {
            with(PV27) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredIB = value.toDouble() * COEF_SHUNT_PV27_PV28
                    testModel.measuredData.I_V_MPT.value = testModel.measuredIB.autoformat()
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
//            sleep(20000)
//            turnOffTM1()
        }
        if (isRunning) {
            turnOffTM1()
            waitUntilFIToLoad()
            regulateTM1(200) // 0.25f = 200 вольт
            startFI(
                testModel.specifiedU_Y_MPT.toInt()/* запас 20% по напряжению для регулирования в функции */,
                50 /* изменять также в regulateFI */
            )
            regulateFI(200)
            waitUntilFIToRun()
            // TODO поставить нужное каждому
        }
        if (isRunning) {
            waiting()
        }
        storeTestValues()
        stopFI(CM.device(UZ91))
        turnOffTM1()
    }

    private fun turnOnTM1(percent: Int) {
        CM.device<PR>(DD2).setUOnTM1((percent * 96 / 100 + 2).toFloat() / 100) // 0.25f  = 196+- постоянки
    }

    private fun regulateTM1(voltage: Int) {
        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Выставление напряжения на ОВ")
            var percent = 0
            // TODO testModel.measuredU - поставить нужное +-
            while (isRunning && (testModel.measuredUB < voltage * 0.8
                        || testModel.measuredUB > voltage * 1.2)
            ) {
                if (isRunning && testModel.measuredUB < voltage * 0.8) {
                    percent += 1
                    turnOnTM1(percent)
                } else if (testModel.measuredUB > voltage * 1.2) {
                    percent -= 1
                    turnOnTM1(percent)
                }
                sleep(500)
            }
            while (isRunning && (testModel.measuredUB < voltage * 0.97
                        || testModel.measuredUB > voltage * 1.03)
            ) {
                if (isRunning && testModel.measuredUB < voltage * 0.97) {
                    percent += 1
                    turnOnTM1(percent)
                } else if (testModel.measuredUB > voltage * 1.03) {
                    percent -= 1
                    turnOnTM1(percent)
                }
                sleep(1000)
            }
        }
        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Напряжение выставлено")
        }
    }

    private fun regulateFI(voltage: Int) {
        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Выставление напряжения на ОЯ")
            var percent = 50
            // TODO testModel.measuredU - поставить нужное +-
            while (isRunning && (testModel.measuredUA < voltage * 0.8
                        || testModel.measuredUA > voltage * 1.2)
            ) {
                if (isRunning && testModel.measuredUA < voltage * 0.8) {
                    percent += 1
                    CM.device<Danfoss>(CM.DeviceID.UZ91).setObjectPercent(percent)
                } else if (testModel.measuredUA > voltage * 1.2) {
                    percent -= 1
                    CM.device<Danfoss>(CM.DeviceID.UZ91).setObjectPercent(percent)
                }
                sleep(500)
            }
            while (isRunning && (testModel.measuredUA < voltage * 0.98
                        || testModel.measuredUA > voltage * 1.02)
            ) {
                if (isRunning && testModel.measuredUA < voltage * 0.98) {
                    percent += 1
                    CM.device<Danfoss>(CM.DeviceID.UZ91).setObjectPercent(percent)
                } else if (testModel.measuredUA > voltage * 1.02) {
                    percent -= 1
                    CM.device<Danfoss>(CM.DeviceID.UZ91).setObjectPercent(percent)
                }
                sleep(1000)
            }
        }
        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Напряжение выставлено")
        }
    }


    private fun turnOffTM1() {
        CM.device<PR>(DD2).setUOnTM1(0f)
    }

    private fun turnOnCircuit() {
        appendMessageToLog(LogTag.INFO, "Сбор схемы")
        CM.device<PR>(DD2).onKM1()
        sleep(200)
    }

    private fun waiting() {
        appendMessageToLog(LogTag.INFO, "Ожидание...")
        sleepWhileRun(testModel.specifiedIDLE_TIME.toInt(), progressProperty = testModel.progressProperty)
    }

    private fun storeTestValues() {
        testModel.storedData.U.value =
            testModel.measuredData.U.value // TODO проверить здесь и в остальных местах всё ли я сохраняю
        testModel.storedData.U_Y_MPT.value = testModel.measuredData.U_Y_MPT.value
        testModel.storedData.U_V_MPT.value = testModel.measuredData.U_V_MPT.value
        testModel.storedData.UAB.value = testModel.measuredData.UAB.value
        testModel.storedData.UBC.value = testModel.measuredData.UBC.value
        testModel.storedData.UCA.value = testModel.measuredData.UCA.value

        testModel.storedData.I.value = testModel.measuredData.I.value
        testModel.storedData.I_Y_MPT.value = testModel.measuredData.I_Y_MPT.value
        testModel.storedData.I_V_MPT.value = testModel.measuredData.I_V_MPT.value
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
        testModel.measuredData.I_Y_MPT.value = testModel.storedData.I_Y_MPT.value
        testModel.measuredData.I_V_MPT.value = testModel.storedData.I_V_MPT.value
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

        reportFields["U_V_MEAS_IDLE"] = testModel.measuredData.U_V_MPT.value
        reportFields["I_V_MEAS_IDLE"] = testModel.measuredData.I_V_MPT.value
        reportFields["U_Y_MEAS_IDLE"] = testModel.measuredData.U_Y_MPT.value
        reportFields["I_Y_MEAS_IDLE"] = testModel.measuredData.I_Y_MPT.value
        reportFields["TIME_MEAS_IDLE"] = testModel.measuredData.time.value
        reportFields["TEMP_AMB_IDLE"] = testModel.measuredData.tempAmb.value
        reportFields["TEMP_TI_IDLE"] = testModel.measuredData.tempTI.value

        reportFields["RESULT_IDLE"] = testModel.measuredData.result.value

        super.saveProtocol()
    }
}
