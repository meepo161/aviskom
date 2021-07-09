package ru.avem.stand.modules.r.tests.pi.load

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.i.views.showTwoWayDialog
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.devices.avem.avem3.AVEM3Model
import ru.avem.stand.modules.r.communication.model.devices.danfoss.Danfoss
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.owen.trm202.TRM202Model
import ru.avem.stand.modules.r.communication.model.devices.satec.pm130.PM130Model
import ru.avem.stand.modules.r.tests.KSPADTest
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.find
import tornadofx.runLater
import java.lang.Thread.sleep
import kotlin.collections.set
import kotlin.math.abs

class Load : KSPADTest(view = LoadView::class, reportTemplate = "load.xlsx") {
    override val name = "Испытание с нагрузкой"

    override val testModel = LoadModel

    var percentFI = 1
    var percentTM2 = 0

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

        testModel.isContinueAgree = false

        testModel.maxTemp = -999.0

        testModel.isNeedAbsTorque = false
    }

    override fun initView() {
        super.initView()

        runLater {
            testModel.progressProperty.value = -1.0

            testModel.measuredData.U_Y_MPT.value = ""
            testModel.measuredData.U_V_MPT.value = ""

            testModel.measuredData.I_Y_MPT.value = ""
            testModel.measuredData.I_V_MPT.value = ""

            testModel.measuredData.U_V_SG.value = ""
            testModel.measuredData.I_V_SG.value = ""

            testModel.measuredData.U.value = ""
            testModel.measuredData.UAB.value = ""
            testModel.measuredData.UBC.value = ""
            testModel.measuredData.UCA.value = ""

            testModel.measuredData.I.value = ""
            testModel.measuredData.IA.value = ""
            testModel.measuredData.IB.value = ""
            testModel.measuredData.IC.value = ""

            testModel.measuredData.F.value = ""

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
            with(CM.DeviceID.PAV41) {
                addCheckableDevice(this)
                CM.startPoll(this, PM130Model.U_AB_REGISTER) { value ->
                    testModel.measuredData.UAB.value = value.toDouble().autoformat()
                    testModel.measuredU =
                        (testModel.measuredData.UAB.value.toDoubleOrDefault(0.0) + testModel.measuredData.UBC.value.toDoubleOrDefault(
                            0.0
                        ) + testModel.measuredData.UCA.value.toDoubleOrDefault(0.0)) / 3.0
                    testModel.measuredData.U.value = testModel.measuredU.autoformat()
                }
                CM.startPoll(this, PM130Model.U_BC_REGISTER) { value ->
                    testModel.measuredData.UBC.value = value.toDouble().autoformat()
                    testModel.measuredU =
                        (testModel.measuredData.UAB.value.toDoubleOrDefault(0.0) + testModel.measuredData.UBC.value.toDoubleOrDefault(
                            0.0
                        ) + testModel.measuredData.UCA.value.toDoubleOrDefault(0.0)) / 3.0
                    testModel.measuredData.U.value = testModel.measuredU.autoformat()
                }
                CM.startPoll(this, PM130Model.U_CA_REGISTER) { value ->
                    testModel.measuredData.UCA.value = value.toDouble().autoformat()
                    testModel.measuredU =
                        (testModel.measuredData.UAB.value.toDoubleOrDefault(0.0) + testModel.measuredData.UBC.value.toDoubleOrDefault(
                            0.0
                        ) + testModel.measuredData.UCA.value.toDoubleOrDefault(0.0)) / 3.0
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
                CM.startPoll(this, PM130Model.F_REGISTER) { value ->
                    if (abs(value.toDouble() * 30) - 400 > 0) {
                        testModel.measuredF = abs(value.toDouble() * 30) - 350
                    } else {
                        testModel.measuredF = 0.0
                    }
                    testModel.measuredData.F.value = testModel.measuredF.autoformat()
                }
            }
        }

        if (isRunning) {
            with(CM.DeviceID.PV23) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredU_Y_MPT = abs(value.toDouble())
                    testModel.measuredData.U_Y_MPT.value = testModel.measuredU_Y_MPT.autoformat()
                }
            }
        }
// 10% = 0,12
        if (isRunning) {
            with(CM.DeviceID.PV25) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredData.U_V_MPT.value = abs(value.toDouble()).autoformat()
                    testModel.measuredU_V_MPT = testModel.measuredData.U_V_MPT.value.toDoubleOrDefault(0.0)
                }
            }
        }

        if (isRunning) {
            with(CM.DeviceID.PV24) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredI_Y_MPT = abs(value.toDouble() * COEF_SHUNT_PV24)
                    testModel.measuredData.I_Y_MPT.value = testModel.measuredI_Y_MPT.autoformat()
                }
            }
        }

        if (isRunning) {
            with(CM.DeviceID.PV27) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredI_V_MPT = abs(value.toDouble() * COEF_SHUNT_PV27_PV28)
                    testModel.measuredData.I_V_MPT.value = testModel.measuredI_V_MPT.autoformat()
                }
            }
        }
        if (isRunning) {
            with(CM.DeviceID.PV26) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredU_V_SG = abs(value.toDouble())
                    testModel.measuredData.U_V_SG.value = testModel.measuredU_V_SG.autoformat()
                }
            }
        }
        if (isRunning) {
            with(CM.DeviceID.PV28) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredI_V_SG = abs(value.toDouble() * COEF_SHUNT_PV27_PV28)
                    testModel.measuredData.I_V_SG.value = testModel.measuredI_V_SG.autoformat()
                }
            }
        }

        if (isRunning) {
            with(CM.DeviceID.PS81) {
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
        percentTM2 = 0
        percentFI = 1
        if (isRunning) {
            turnOnCircuit()
        }
        if (isRunning) {
            turnOffTM1()
            turnOffTM2()
        }

//        while (isRunning) {
//            sleep(1000)
//        }

        if (isRunning) {
            waitUntilFIToLoad()
        }

        if (isRunning) {
            regulateTM1(testModel.specifiedU_V_MPT.toInt())
        }

        if (isRunning) {
            startFI(300, 1)
        }

//        if (isRunning) {
//            waitUntilFIToRun()
//        }

        if (isRunning) {
            regulateFI(testModel.specifiedU_Y_MPT.toInt())
        }

        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Ожидание разгона МПТ")
            sleepWhileRun(15)
        }
        if (isRunning) {
            turnOnLoad()
        }
        if (isRunning) {
//            regulateTM2(testModel.specifiedI_Y_MPT.toInt())
            regulateTM2ForVoltage(testModel.specifiedU_V_SG.toInt())
        }
        if (isRunning) {
            waiting()
        }
        percentTM2 = 0
        percentFI = 1
        storeTestValues()
        stopFI(CM.device(CM.DeviceID.UZ91))
        turnOffTM1()
        turnOffTM2()
    }

    private fun regulateTM1(voltage: Int, current: Double = 9999.0) {
        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Выставление напряжения на ОВ МПТ")
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
                sleep(1000)
            }
            while (isRunning && (testModel.measuredU_V_MPT < voltage * 1 || testModel.measuredU_V_MPT > voltage * 1.06)) {

                if (isRunning && testModel.measuredU_V_MPT < voltage * 1) {
                    percent += 1
                    turnOnTM1(percent)
                } else if (testModel.measuredU_V_MPT > voltage * 1.06) {
                    percent -= 1
                    turnOnTM1(percent)
                }
                sleep(1000)
            }
        }
        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Напряжение на ОВ МПТ выставлено")
        }
    }

    private fun regulateFI(voltage: Int) {
        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Выставление напряжения на ОЯ")
            var percent = percentFI
            while (isRunning && (testModel.measuredU_Y_MPT < voltage * 0.8
                        || testModel.measuredU_Y_MPT > voltage * 1.2)
            ) {
                if (isRunning && testModel.measuredU_Y_MPT < voltage * 0.8) {
                    percent += 1
                    CM.device<Danfoss>(CM.DeviceID.UZ91).setObjectPercent(percent)
                } else if (testModel.measuredU_Y_MPT > voltage * 1.2) {
                    percent -= 1
                    CM.device<Danfoss>(CM.DeviceID.UZ91).setObjectPercent(percent)
                }
                sleep(500)
            }
            while (isRunning && (testModel.measuredU_Y_MPT < voltage * 0.98
                        || testModel.measuredU_Y_MPT > voltage * 1.02)
            ) {
                if (isRunning && testModel.measuredU_Y_MPT < voltage * 0.98) {
                    percent += 1
                    CM.device<Danfoss>(CM.DeviceID.UZ91).setObjectPercent(percent)
                } else if (testModel.measuredU_Y_MPT > voltage * 1.02) {
                    percent -= 1
                    CM.device<Danfoss>(CM.DeviceID.UZ91).setObjectPercent(percent)
                }
                sleep(1000)
            }
            percentFI = percent
        }
        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Напряжение на ОЯ выставлено")
        }
    }

    private fun turnOnCircuit() {
        appendMessageToLog(LogTag.INFO, "Сбор схемы")
        CM.device<PR>(CM.DeviceID.DD2).onKM1()
        sleep(200)
    }

    private fun turnOnLoad() {
        appendMessageToLog(LogTag.INFO, "Сбор схемы нагрузки")
        CM.device<PR>(CM.DeviceID.DD2).onRotateKM2()
        sleep(200)
//        if (testModel.specifiedI_Y_MPT > 10) {
//        CM.device<PR>(CM.DeviceID.DD2).onUNM55KM81()
//        } else {
        CM.device<PR>(CM.DeviceID.DD2).onUNM15KM82()
//        }
        sleep(200)
        CM.device<PR>(CM.DeviceID.DD2).onVentilyatorUNMKKM81()
        sleep(5000)
    }

    private fun turnOnTM1(percent: Int) {
        CM.device<PR>(CM.DeviceID.DD2).setUOnTM1((percent * 96 / 100 + 2).toFloat() / 100) // 0.25f  = 196+- постоянки
    }

    private fun turnOffTM1() {
        CM.device<PR>(CM.DeviceID.DD2).setUOnTM1(0f)
    }

    private fun turnOnTM2(percent: Int) {
        CM.device<PR>(CM.DeviceID.DD2).setUOnTM2((percent * 96 / 100 + 2).toFloat() / 100)
    }

    private fun turnOffTM2() {
        CM.device<PR>(CM.DeviceID.DD2).setUOnTM2(0f)
    }

    private fun showZDialog(zTI: Int, zLM: Int) {
        try {
            showTwoWayDialog(
                "Внимание",
                "На ОИ был установлен шкив $zTI и на НМ был установлен шкив $zLM?",
                "ДА",
                "НЕТ",
                { },
                { cause = "Ошибка при установке шкивов" },
                200_000,
                { false },
                find(view).currentWindow!!
            )
        } catch (e: Exception) {
            appendMessageToLog(LogTag.DEBUG, "Диалог не был обработан в течение 200с")
        }
    }

    private fun regulateTM2(amperage: Int) {
        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Выставление напряжения на ОВ СГ")
            var percent = percentTM2
            // TODO testModel.measuredI2A - поставить нужное +-
            while (isRunning && (testModel.measuredI_Y_MPT < amperage * 0.8
                        || testModel.measuredI_Y_MPT > amperage * 1.2)
            ) {
                if (isRunning && testModel.measuredI_Y_MPT < amperage * 0.8) {
                    percent += 1
                    turnOnTM2(percent)
                } else if (testModel.measuredI_Y_MPT > amperage * 1.2) {
                    percent -= 1
                    turnOnTM2(percent)
                }
                sleep(2500)
            }
            while (isRunning && (testModel.measuredI_Y_MPT < amperage * 1
                        || testModel.measuredI_Y_MPT > amperage * 1.1)
            ) {
                if (isRunning && testModel.measuredI_Y_MPT < amperage * 1) {
                    percent += 1
                    turnOnTM2(percent)
                } else if (testModel.measuredI_Y_MPT > amperage * 1.1) {
                    percent -= 1
                    turnOnTM2(percent)
                }
                sleep(4000)
            }
            percentTM2 = percent
        }
        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Напряжение выставлено")
        }
    }

    private fun regulateTM2ForVoltage(voltage: Int) {
        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Выставление напряжения на ОВ СГ")
            var percent = percentTM2
            // TODO testModel.measuredI2A - поставить нужное +-
            while (isRunning && (testModel.measuredU < voltage * 0.8
                        || testModel.measuredU > voltage * 1.2)
            ) {
                if (isRunning && testModel.measuredU < voltage * 0.8) {
                    percent += 1
                    turnOnTM2(percent)
                } else if (testModel.measuredU > voltage * 1.2) {
                    percent -= 1
                    turnOnTM2(percent)
                }
                sleep(2500)
            }
            while (isRunning && (testModel.measuredU < voltage * 0.95
                        || testModel.measuredU > voltage * 1.05)
            ) {
                if (isRunning && testModel.measuredU < voltage * 0.95) {
                    percent += 1
                    turnOnTM2(percent)
                } else if (testModel.measuredU > voltage * 1.05) {
                    percent -= 1
                    turnOnTM2(percent)
                }
                sleep(4000)
            }
            percentTM2 = percent
        }
        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Напряжение выставлено")
        }
    }

    private fun waiting() {
        appendMessageToLog(LogTag.INFO, "Ожидание...")
        sleepWhileRun(testModel.specifiedLOAD_TIME.toInt(), progressProperty = testModel.progressProperty)
    }


    override fun result() {
        super.result()

        when {
            !isSuccess -> {
                testModel.measuredData.result.value = "Прервано"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
            }
            testModel.storedData.UAB.value.toDouble() > testModel.storedData.UBC.value.toDouble() * 1.03 ||
                    testModel.storedData.UBC.value.toDouble() > testModel.storedData.UCA.value.toDouble() * 1.03 ||
                    testModel.storedData.UAB.value.toDouble() > testModel.storedData.UCA.value.toDouble() * 1.03 ||
                    testModel.storedData.UAB.value.toDouble() < testModel.storedData.UBC.value.toDouble() * 0.97 ||
                    testModel.storedData.UBC.value.toDouble() < testModel.storedData.UCA.value.toDouble() * 0.97 ||
                    testModel.storedData.UAB.value.toDouble() < testModel.storedData.UCA.value.toDouble() * 0.97
            -> {
                appendMessageToLog(LogTag.ERROR, "Испытание завершено. Асимметрия напряжений больше 3%")
                testModel.measuredData.result.value = "Не соответствует"
            }
            else -> {
                testModel.measuredData.result.value = "Соответствует"
                appendMessageToLog(LogTag.INFO, "Испытание завершено успешно")
            }
        }
    }

    override fun finalizeView() {
        super.finalizeView()
        restoreTestValues()
        runLater {
            testModel.progressProperty.value = 0.0
        }
    }

    private fun storeTestValues() {
        testModel.storedData.U_Y_MPT.value = testModel.measuredData.U_Y_MPT.value
        testModel.storedData.U_V_MPT.value = testModel.measuredData.U_V_MPT.value

        testModel.storedData.I_Y_MPT.value = testModel.measuredData.I_Y_MPT.value
        testModel.storedData.I_V_MPT.value = testModel.measuredData.I_V_MPT.value

        testModel.storedData.U_V_SG.value = testModel.measuredData.U_V_SG.value
        testModel.storedData.I_V_SG.value = testModel.measuredData.I_V_SG.value

        testModel.storedData.U.value = testModel.measuredData.U.value
        testModel.storedData.UAB.value = testModel.measuredData.UAB.value
        testModel.storedData.UBC.value = testModel.measuredData.UBC.value
        testModel.storedData.UCA.value = testModel.measuredData.UCA.value

        testModel.storedData.I.value = testModel.measuredData.I.value
        testModel.storedData.IA.value = testModel.measuredData.IA.value
        testModel.storedData.IB.value = testModel.measuredData.IB.value
        testModel.storedData.IC.value = testModel.measuredData.IC.value

        testModel.storedData.P1.value = testModel.measuredData.P1.value
        testModel.storedData.F.value = testModel.measuredData.F.value

    }

    private fun restoreTestValues() {
        testModel.measuredData.U_Y_MPT.value = testModel.storedData.U_Y_MPT.value
        testModel.measuredData.U_V_MPT.value = testModel.storedData.U_V_MPT.value

        testModel.measuredData.I_Y_MPT.value = testModel.storedData.I_Y_MPT.value
        testModel.measuredData.I_V_MPT.value = testModel.storedData.I_V_MPT.value

        testModel.measuredData.U_V_SG.value = testModel.storedData.U_V_SG.value
        testModel.measuredData.I_V_SG.value = testModel.storedData.I_V_SG.value

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
    }

    override fun saveProtocol() {
        reportFields["TEST_NAME_LOAD"] = name

        reportFields["L1_U_LOAD"] = testModel.measuredData.UAB.value
        reportFields["L2_U_LOAD"] = testModel.measuredData.UBC.value
        reportFields["L3_U_LOAD"] = testModel.measuredData.UCA.value
        reportFields["L1_I_LOAD"] = testModel.measuredData.IA.value
        reportFields["L2_I_LOAD"] = testModel.measuredData.IB.value
        reportFields["L3_I_LOAD"] = testModel.measuredData.IC.value
        reportFields["U_V_SG_LOAD"] = testModel.measuredData.U_V_SG.value
        reportFields["I_V_SG_LOAD"] = testModel.measuredData.I_V_SG.value
        reportFields["U_V_MEAS_LOAD"] = testModel.measuredData.U_V_MPT.value
        reportFields["I_V_MEAS_LOAD"] = testModel.measuredData.I_V_MPT.value
        reportFields["U_Y_MEAS_LOAD"] = testModel.measuredData.U_Y_MPT.value
        reportFields["I_Y_MEAS_LOAD"] = testModel.measuredData.I_Y_MPT.value
        reportFields["TIME_MEAS_LOAD"] = testModel.measuredData.time.value
        reportFields["TEMP_AMB_LOAD"] = testModel.measuredData.tempAmb.value
        reportFields["TEMP_TI_LOAD"] = testModel.measuredData.tempTI.value
        reportFields["FREQ_LOAD"] = testModel.measuredData.F.value
        reportFields["RESULT_LOAD"] = testModel.measuredData.result.value

        super.saveProtocol()
    }
}
