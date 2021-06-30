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

        testModel.isContinueAgree = false

        testModel.maxTemp = -999.0

        testModel.isNeedAbsTorque = false
    }

    override fun initView() {
        super.initView()

        runLater {
            testModel.progressProperty.value = -1.0

            testModel.measuredData.U.value = ""
            testModel.measuredData.UMPTOY.value = ""
            testModel.measuredData.UMPTOV.value = ""
            testModel.measuredData.UAB.value = ""
            testModel.measuredData.UBC.value = ""
            testModel.measuredData.UCA.value = ""

            testModel.measuredData.I.value = ""
            testModel.measuredData.IA.value = ""
            testModel.measuredData.IB.value = ""
            testModel.measuredData.IC.value = ""

            testModel.measuredData.P1.value = ""
            testModel.measuredData.P2.value = ""

            testModel.measuredData.result.value = ""

            testModel.measuredData.time.value = ""
            testModel.measuredData.torque.value = ""
            testModel.measuredData.RPM.value = ""
            testModel.measuredData.efficiency.value = ""
            testModel.measuredData.sk.value = ""
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

                CM.startPoll(this, PM130Model.P_REGISTER) { value ->
                    testModel.measuredP1 = abs(value.toDouble() * CURRENT_STAGE_PM130)
                    testModel.measuredData.P1.value = testModel.measuredP1.autoformat()
                }
            }
        }

        if (isRunning) {
            with(CM.DeviceID.PV23) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredUA = value.toDouble()
                    testModel.measuredData.UMPTOY.value = testModel.measuredUA.autoformat()
                }
            }
        }
// 10% = 0,12
        if (isRunning) {
            with(CM.DeviceID.PV25) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredUB = value.toDouble()
                    testModel.measuredData.UMPTOV.value = testModel.measuredUB.autoformat()
                }
            }
        }

        if (isRunning) {
            with(CM.DeviceID.PV24) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredIMPTOY = value.toDouble() * COEF_SHUNT_PV24
                    testModel.measuredData.IMPTOY.value = testModel.measuredIMPTOY.autoformat()
                }
            }
        }

        if (isRunning) {
            with(CM.DeviceID.PV27) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredIMPTOV = value.toDouble() * COEF_SHUNT_PV27_PV28
                    testModel.measuredData.IMPTOV.value = testModel.measuredIMPTOV.autoformat()
                }
            }
        }
        if (isRunning) {
            with(CM.DeviceID.PV26) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredUSGOV = value.toDouble()
                    testModel.measuredData.USGOV.value = testModel.measuredUSGOV.autoformat()
                }
            }
        }
        if (isRunning) {
            with(CM.DeviceID.PV28) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredISGOV = value.toDouble() * COEF_SHUNT_PV27_PV28
                    testModel.measuredData.ISGOV.value = testModel.measuredISGOV.autoformat()
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
        if (isRunning) {
            turnOnCircuit()
        }
        if (isRunning) {
            turnOffTM1()
            turnOffTM2()
            waitUntilFIToLoad()
            regulateTM1(200) // 0.25f = 200 вольт
            startFI(
                testModel.specifiedU_Y_MPT.toInt()/* запас 20% по напряжению для регулирования в функции */,
                50 /* изменять также в regulateFI */
            )
            regulateFI(200)
            waitUntilFIToRun()
            // TODO поставить нужное каждому +-
        }
        if (isRunning) {
            turnOnLoad()
        }
        if (isRunning) {
            regulateTM2(testModel.specifiedI_Y_MPT.toInt()) // TODO поставить нужное +-
        }
        if (isRunning) {
            waiting()
        }
        storeTestValues()
        if (isRunning) {
            stopFI(CM.device(CM.DeviceID.UZ91))
            turnOffTM1()
            turnOffTM2()
        }
        // TODO опыт икаса
        //    Температура обмотки по сопротивлению на момент измерения = (Rгорячей-Rхолодной)/(Rхолодной*0,004(для меди)) + ТемператураОбмоткиХолодной(ОИ или воздуха)
        // TODO опыт мегера (в главном тз есть)
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

    private fun turnOnCircuit() {
        appendMessageToLog(LogTag.INFO, "Сбор схемы")
        CM.device<PR>(CM.DeviceID.DD2).onKM1()
        sleep(200)
    }

    private fun turnOnLoad() {
        appendMessageToLog(LogTag.INFO, "Сбор схемы нагрузки")
        CM.device<PR>(CM.DeviceID.DD2).onRotateKM2()
        sleep(200)
        CM.device<PR>(CM.DeviceID.DD2).onUNM15KM82()
        sleep(200)
        CM.device<PR>(CM.DeviceID.DD2).onRotateUNMKKM81()
        sleep(5000)
    }

    private fun turnOnTM1(percent: Int) {
        CM.device<PR>(CM.DeviceID.DD2).setUOnTM1((percent * 96 / 100 + 2).toFloat() / 100) // 0.25f  = 196+- постоянки
    }

    private fun turnOffTM1() {
        CM.device<PR>(CM.DeviceID.DD2).setUOnTM1(0f)
    }

    private fun turnOnTM2(percent: Int) {
        CM.device<PR>(CM.DeviceID.DD2).setUOnTM1((percent * 96 / 100 + 2).toFloat() / 100)
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
            appendMessageToLog(LogTag.INFO, "Выставление напряжения на ОВ")
            var percent = 0
            // TODO testModel.measuredI2A - поставить нужное +-
            while (isRunning && (testModel.measuredIMPTOY < amperage * 0.6
                        || testModel.measuredIMPTOY > amperage * 1)
            ) {
                if (isRunning && testModel.measuredIMPTOY < amperage * 0.6) {
                    percent += 1
                    turnOnTM2(percent)
                } else if (testModel.measuredIMPTOY > amperage * 1) {
                    percent -= 1
                    turnOnTM2(percent)
                }
                sleep(500)
            }
            while (isRunning && (testModel.measuredIMPTOY < amperage * 0.96
                        || testModel.measuredIMPTOY > amperage * 1)
            ) {
                if (isRunning && testModel.measuredIMPTOY < amperage * 0.96) {
                    percent += 1
                    turnOnTM2(percent)
                } else if (testModel.measuredIMPTOY > amperage * 1) {
                    percent -= 1
                    turnOnTM2(percent)
                }
                sleep(1000)
            }
        }
        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Напряжение выставлено")
        }
    }

    private fun waiting() {
        appendMessageToLog(LogTag.INFO, "Ожидание...")
        sleepWhileRun(testModel.specifiedLOAD_TIME.toInt(), progressProperty = testModel.progressProperty)
    }

    private fun storeTestValues() {
        testModel.storedData.UMPTOY.value = testModel.measuredData.UMPTOY.value
        testModel.storedData.UMPTOV.value = testModel.measuredData.UMPTOV.value
        testModel.storedData.IMPTOY.value = testModel.measuredData.IMPTOY.value
        testModel.storedData.IMPTOV.value = testModel.measuredData.IMPTOV.value

        testModel.storedData.USGOV.value = testModel.measuredData.USGOV.value
        testModel.storedData.ISGOV.value = testModel.measuredData.ISGOV.value

        testModel.storedData.U.value = testModel.measuredData.U.value
        testModel.storedData.UAB.value = testModel.measuredData.UAB.value
        testModel.storedData.UBC.value = testModel.measuredData.UBC.value
        testModel.storedData.UCA.value = testModel.measuredData.UCA.value

        testModel.storedData.I.value = testModel.measuredData.I.value
        testModel.storedData.IA.value = testModel.measuredData.IA.value
        testModel.storedData.IB.value = testModel.measuredData.IB.value
        testModel.storedData.IC.value = testModel.measuredData.IC.value

        testModel.storedData.P2.value = testModel.measuredData.P2.value

        testModel.storedData.USGOV.value = testModel.measuredData.USGOV.value
        testModel.storedData.ISGOV.value = testModel.measuredData.ISGOV.value
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
        testModel.measuredData.UMPTOV.value = testModel.storedData.UMPTOV.value
        testModel.measuredData.IMPTOV.value = testModel.storedData.IMPTOV.value
        testModel.measuredData.UMPTOY.value = testModel.storedData.UMPTOY.value
        testModel.measuredData.IMPTOY.value = testModel.storedData.IMPTOY.value

        testModel.measuredData.UAB.value = testModel.storedData.UAB.value
        testModel.measuredData.UBC.value = testModel.storedData.UBC.value
        testModel.measuredData.UCA.value = testModel.storedData.UCA.value
        testModel.measuredData.I.value = testModel.storedData.I.value
        testModel.measuredData.IA.value = testModel.storedData.IA.value
        testModel.measuredData.IB.value = testModel.storedData.IB.value
        testModel.measuredData.IC.value = testModel.storedData.IC.value

        testModel.measuredData.P2.value = testModel.storedData.P2.value
    }

    override fun saveProtocol() {
        reportFields["TEST_NAME_LOAD"] = name

        reportFields["L1_U_LOAD"] =      testModel.measuredData.UAB.value
        reportFields["L2_U_LOAD"] =      testModel.measuredData.UBC.value
        reportFields["L3_U_LOAD"] =      testModel.measuredData.UCA.value
        reportFields["L1_I_LOAD"] =      testModel.measuredData.IA.value
        reportFields["L2_I_LOAD"] =      testModel.measuredData.IB.value
        reportFields["L3_I_LOAD"] =      testModel.measuredData.IC.value
        reportFields["U_V_SG_LOAD"] =    testModel.measuredData.USGOV.value
        reportFields["I_V_SG_LOAD"] =    testModel.measuredData.ISGOV.value
        reportFields["U_V_MEAS_LOAD"] =  testModel.measuredData.UMPTOV.value
        reportFields["I_V_MEAS_LOAD"] =  testModel.measuredData.IMPTOV.value
        reportFields["U_Y_MEAS_LOAD"] =  testModel.measuredData.UMPTOY.value
        reportFields["I_Y_MEAS_LOAD"] =  testModel.measuredData.IMPTOY.value
        reportFields["TIME_MEAS_LOAD"] = testModel.measuredData.time.value
        reportFields["TEMP_AMB_LOAD"] =  testModel.measuredData.tempAmb.value
        reportFields["TEMP_TI_LOAD"] =   testModel.measuredData.tempTI.value
        reportFields["RESULT_LOAD"] =    testModel.measuredData.result.value

        super.saveProtocol()
    }
}
