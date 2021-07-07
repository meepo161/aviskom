package ru.avem.stand.modules.r.tests.psi.idle

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.CM.DeviceID.*
import ru.avem.stand.modules.r.communication.model.devices.avem.avem3.AVEM3Model
import ru.avem.stand.modules.r.communication.model.devices.danfoss.Danfoss
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.owen.trm202.TRM202Model
import ru.avem.stand.modules.r.communication.model.devices.satec.pm130.PM130Model
import ru.avem.stand.modules.r.tests.KSPADTest
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.runLater
import java.lang.Thread.sleep
import kotlin.math.abs

class Idle : KSPADTest(view = IdleView::class, reportTemplate = "idle.xlsx") {
    override val name = "Холостой ход"

    override val testModel = IdleModel

    override fun initVars() {
        super.initVars()

        testModel.specifiedU_Y_MPT = PreFillModel.testTypeProp.value.fields["U_Y_MPT"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedI_Y_MPT = PreFillModel.testTypeProp.value.fields["I_Y_MPT"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedU_V_MPT = PreFillModel.testTypeProp.value.fields["U_V_MPT"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedI_V_MPT = PreFillModel.testTypeProp.value.fields["I_V_MPT"]?.value.toDoubleOrDefault(0.0)
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

            testModel.measuredData.U_Y_MPT.value = ""
            testModel.measuredData.U_V_MPT.value = ""

            testModel.measuredData.I_Y_MPT.value = ""
            testModel.measuredData.I_V_MPT.value = ""

            testModel.measuredData.tempAmb.value = ""
            testModel.measuredData.tempTI.value = ""

            testModel.measuredData.time.value = ""
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
                    testModel.measuredData.U_Y_MPT.value = abs(value.toDouble()).autoformat()
                    testModel.measuredU_Y_MPT = testModel.measuredData.U_Y_MPT.value.toDoubleOrDefault(0.0)
                }
            }
        }
// 10% = 0,02
        if (isRunning) {
            with(PV25) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredData.U_V_MPT.value = abs(value.toDouble()).autoformat()
                    testModel.measuredU_V_MPT = testModel.measuredData.U_V_MPT.value.toDoubleOrDefault(0.0)
                }
            }
        }

        if (isRunning) {
            with(PV24) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredData.I_Y_MPT.value = abs(value.toDouble() * COEF_SHUNT_PV24).autoformat()
                    testModel.measuredI_Y_MPT = testModel.measuredData.I_Y_MPT.value.toDoubleOrDefault(0.0)
                }
            }
        }

        if (isRunning) {
            with(PV27) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredData.I_V_MPT.value = abs(value.toDouble() * COEF_SHUNT_PV27_PV28).autoformat()
                    testModel.measuredI_V_MPT = testModel.measuredData.I_V_MPT.value.toDoubleOrDefault(0.0)
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

        if (isRunning) {
            with(PAV41) {
                addCheckableDevice(this)
                CM.startPoll(this, PM130Model.F_REGISTER) { value ->
                    testModel.measuredF = abs(value.toDouble() * 30)
                    testModel.measuredData.F.value = testModel.measuredF.autoformat()
                }
            }
        }

//        if (isRunning) {
//            with(PV26) {
//                addCheckableDevice(this)
//                CM.startPoll(this, AVEM3Model.FREQ) { value ->
//                    testModel.measuredF = abs(value.toDouble() * 30)
//                    testModel.measuredData.F.value = testModel.measuredF.autoformat()
//                }
//            }
//        }
    }

    override fun logic() {
        if (isRunning) {
            turnOnCircuit()
        }
        if (isRunning) {
            turnOffTM1()
            turnOffTM2()
            waitUntilFIToLoad()
            turnOnTM2(10)
        }

        if (isRunning) {
            regulateTM1(testModel.specifiedU_V_MPT.toInt())
        }

        if (isRunning) {
            startFI(300, 1)
        }

        if (isRunning) {
            regulateFI(testModel.specifiedU_Y_MPT.toInt())
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

    private fun regulateTM1(voltage: Int, current: Double = 9999.0) {
        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Выставление напряжения на ОВ")
            var percent = 0
            // TODO testModel.measuredU - поставить нужное +-
            while (isRunning && (testModel.measuredU_V_MPT < voltage * 0.8 || testModel.measuredU_V_MPT > voltage * 1.2)) {

                if (isRunning && testModel.measuredU_V_MPT < voltage * 0.8) {
                    percent += 1
                    turnOnTM1(percent)
                } else if (testModel.measuredU_V_MPT > voltage * 1.2) {
                    percent -= 1
                    turnOnTM1(percent)
                }
                sleep(200)
            }
            while (isRunning && (testModel.measuredU_V_MPT < voltage * 1 || testModel.measuredU_V_MPT > voltage * 1.06)) {

                if (isRunning && testModel.measuredU_V_MPT < voltage * 1) {
                    percent += 1
                    turnOnTM1(percent)
                } else if (testModel.measuredU_V_MPT > voltage * 1.06) {
                    percent -= 1
                    turnOnTM1(percent)
                }
                sleep(400)
            }
        }
        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Напряжение на ОВ выставлено")
        }
    }

    private fun regulateFI(voltage: Int) {
        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Выставление напряжения на ОЯ")
            var percent = 5
            // TODO testModel.measuredU - поставить нужное +-
            while (isRunning && (testModel.measuredU_Y_MPT < voltage * 0.8
                        || testModel.measuredU_Y_MPT > voltage * 1.2)
            ) {
                if (isRunning && testModel.measuredU_Y_MPT < voltage * 0.8) {
                    percent += 1
                    CM.device<Danfoss>(UZ91).setObjectPercent(percent)
                } else if (testModel.measuredU_Y_MPT > voltage * 1.2) {
                    percent -= 1
                    CM.device<Danfoss>(UZ91).setObjectPercent(percent)
                }
                sleep(500)
            }
            while (isRunning && (testModel.measuredU_Y_MPT < voltage * 0.98
                        || testModel.measuredU_Y_MPT > voltage * 1.02)
            ) {
                if (isRunning && testModel.measuredU_Y_MPT < voltage * 0.98) {
                    percent += 1
                    CM.device<Danfoss>(UZ91).setObjectPercent(percent)
                } else if (testModel.measuredU_Y_MPT > voltage * 1.02) {
                    percent -= 1
                    CM.device<Danfoss>(UZ91).setObjectPercent(percent)
                }
                sleep(500)
            }
        }
        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Напряжение на ОЯ выставлено")
        }
    }


    private fun turnOffTM1() {
        CM.device<PR>(DD2).setUOnTM1(0f)
    }

    private fun turnOnTM2(percent: Int) {
        CM.device<PR>(DD2).setUOnTM2((percent * 96 / 100 + 2).toFloat() / 100)
    }

    private fun turnOffTM2() {
        CM.device<PR>(DD2).setUOnTM2(0f)
    }

    private fun turnOnCircuit() {
        appendMessageToLog(LogTag.INFO, "Сбор схемы")
        CM.device<PR>(DD2).onKM1()
        sleep(200)
        appendMessageToLog(LogTag.INFO, "Подключение генератора")
        CM.device<PR>(DD2).onRotateKM2()
    }

    private fun waiting() {
        appendMessageToLog(LogTag.INFO, "Ожидание...")
        sleepWhileRun(testModel.specifiedIDLE_TIME.toInt(), progressProperty = testModel.progressProperty)
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

    private fun storeTestValues() {// TODO проверить здесь и в остальных местах всё ли я сохраняю
        testModel.storedData.U_Y_MPT.value = testModel.measuredData.U_Y_MPT.value
        testModel.storedData.U_V_MPT.value = testModel.measuredData.U_V_MPT.value

        testModel.storedData.I_Y_MPT.value = testModel.measuredData.I_Y_MPT.value
        testModel.storedData.I_V_MPT.value = testModel.measuredData.I_V_MPT.value

        testModel.storedData.tempAmb.value = testModel.measuredData.tempAmb.value
        testModel.storedData.tempTI.value = testModel.measuredData.tempTI.value
        testModel.storedData.F.value = testModel.measuredData.F.value
    }

    private fun restoreTestValues() {
        testModel.measuredData.U_Y_MPT.value = testModel.storedData.U_Y_MPT.value
        testModel.measuredData.U_V_MPT.value = testModel.storedData.U_V_MPT.value

        testModel.measuredData.I_Y_MPT.value = testModel.storedData.I_Y_MPT.value
        testModel.measuredData.I_V_MPT.value = testModel.storedData.I_V_MPT.value

        testModel.measuredData.tempAmb.value = testModel.storedData.tempAmb.value
        testModel.measuredData.tempTI.value = testModel.storedData.tempTI.value
        testModel.measuredData.F.value = testModel.storedData.F.value
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
        reportFields["FREQ_IDLE"] = testModel.measuredData.F.value

        reportFields["RESULT_IDLE"] = testModel.measuredData.result.value

        super.saveProtocol()
    }
}
