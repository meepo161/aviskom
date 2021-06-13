package ru.avem.stand.modules.r.tests.pi.maxm

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.i.views.showTwoWayDialog
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.devices.danfoss.Danfoss
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.satec.pm130.PM130Model
import ru.avem.stand.modules.r.tests.KSPADTest
import ru.avem.stand.modules.r.tests.calcSyncRPM
import ru.avem.stand.modules.r.tests.calcZs
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.*
import java.lang.Thread.sleep
import kotlin.math.abs

class MaxM : KSPADTest(view = MaxMView::class, reportTemplate = "maxm.xlsx") {
    override val name = "Определение максимального вращающего момента"

    override val testModel = MaxMModel

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

        testModel.syncRPM = calcSyncRPM(testModel.specifiedF.toInt(), testModel.specifiedRPM.toInt())

        testModel.isContinueAgree = false

        testModel.isNeedAbsTorque = false
        testModel.measuredTorqueMax = -1.0
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
            testModel.measuredData.P2.value = ""

            testModel.measuredData.result.value = ""

            testModel.measuredData.torque.value = ""
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
                        (testModel.measuredData.UAB.value.toDoubleOrDefault(0.0) + testModel.measuredData.UAB.value.toDoubleOrDefault(
                            0.0
                        ) + testModel.measuredData.UAB.value.toDoubleOrDefault(0.0)) / 3.0
                    testModel.measuredData.U.value = testModel.measuredU.autoformat()
                }
                CM.startPoll(this, PM130Model.U_BC_REGISTER) { value ->
                    testModel.measuredData.UBC.value = value.toDouble().autoformat()
                    testModel.measuredU =
                        (testModel.measuredData.UAB.value.toDoubleOrDefault(0.0) + testModel.measuredData.UAB.value.toDoubleOrDefault(
                            0.0
                        ) + testModel.measuredData.UAB.value.toDoubleOrDefault(0.0)) / 3.0
                    testModel.measuredData.U.value = testModel.measuredU.autoformat()
                }
                CM.startPoll(this, PM130Model.U_CA_REGISTER) { value ->
                    testModel.measuredData.UCA.value = value.toDouble().autoformat()
                    testModel.measuredU =
                        (testModel.measuredData.UAB.value.toDoubleOrDefault(0.0) + testModel.measuredData.UAB.value.toDoubleOrDefault(
                            0.0
                        ) + testModel.measuredData.UAB.value.toDoubleOrDefault(0.0)) / 3.0
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
                    testModel.measuredP1 = testModel.measuredData.P1.value.toDouble()
                }
            }
        }

    }

    override fun logic() {
        calcF()
        if (isRunning) {
//            CM.device<T42>(CM.DeviceID.BT100).setAveragingPoints(100)
        }
        if (isRunning) {
            turnOnCircuit()
        }
        if (isRunning) {
            waitUntilFIToLoad()
        }
        if (isRunning) {
            checkLMDirection()
        }
        if (isRunning) {
            startTIFI()
            waitUntilFIToRun()
        }
        if (isRunning) {
            startLMFI()
        }
        if (isRunning) {
            findMaxTorque()
        }
        sleepWhileRun(1)
        storeTestValues()
        if (isRunning) {
                CM.device<PR>(CM.DeviceID.DD2).offShuntirGB30()
            stopFI(CM.device(CM.DeviceID.UZ91))
        }
        if (isRunning) {
//            CM.device<T42>(CM.DeviceID.BT100).setAveragingPoints(5000)
        }
    }

    private fun calcF() {
        val (zTI, zLM) = calcZs(testModel.syncRPM.toInt())
        showZDialog(zTI.toInt(), zLM.toInt())
        val nTI = testModel.specifiedRPM // TODO реальное значение?
        val fNom = testModel.specifiedF
        val nLM = 1480.0

        testModel.fLM = (nTI * zTI * fNom) / (nLM * zLM)
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

    private fun turnOnCircuit() {
        appendMessageToLog(LogTag.INFO, "Сбор схемы")
        CM.device<PR>(CM.DeviceID.DD2).onIkasKM61()
        sleep(200)
//        CM.device<PR>(CM.DeviceID.DD2).onMaxAmperageStage()
        
        sleep(200)
//        CM.device<PR>(CM.DeviceID.DD2).fromFI()
        sleep(200)
    }

    private fun checkLMDirection() {
        appendMessageToLog(LogTag.INFO, "Проверка направления вращения НМ...")
//        CM.device<C2000>(CM.DeviceID.UZ92).setObjectParams(
//            fOut = 50,
//
//            voltageP1 = 100,
//            fP1 = 50,
//
//            voltageP2 = 1,
//            fP2 = 1
//        )
//        CM.device<C2000>(CM.DeviceID.UZ92).startObject()
        sleepWhileRun(10)
        testModel.isLMDirectionRight = testModel.measuredTorque > 0.0
//        CM.device<C2000>(CM.DeviceID.UZ92).stopObject()
        sleepWhileRun(10)
    }

    private fun startTIFI() {
        appendMessageToLog(LogTag.INFO, "Разгон ЧП...")
        CM.device<Danfoss>(CM.DeviceID.UZ91).setObjectParams(
            voltage = 100,
            percentF = 100,
        )
        CM.device<Danfoss>(CM.DeviceID.UZ91).startObject()
    }

    private fun startLMFI() {
        appendMessageToLog(LogTag.INFO, "Разгон НМ...")

        var u = 5
        val maxU = 380

//        CM.device<C2000>(CM.DeviceID.UZ92).setObjectParams(
//            fOut = testModel.fLM,
//
//            voltageP1 = u,
//            fP1 = testModel.fLM,
//
//            voltageP2 = 1,
//            fP2 = 1
//        )
        testModel.isTIDirectionRight = testModel.measuredTorque < 0.0
        val lmDirection = if (!testModel.isLMDirectionRight xor !testModel.isTIDirectionRight) {
            appendMessageToLog(LogTag.INFO, "Реверс НМ")
            Danfoss.Direction.REVERSE
        } else {
            Danfoss.Direction.FORWARD
        }
//        CM.device<C2000>(CM.DeviceID.UZ92).startObject(lmDirection)

        testModel.isNeedAbsTorque = true

        while (isRunning && u < maxU) {
            u++
//            CM.device<C2000>(CM.DeviceID.UZ92).setObjectUMax(u)
            sleep(50)
        }

//        CM.device<C2000>(CM.DeviceID.UZ92).setObjectUMax(maxU)
    }

    private fun findMaxTorque(step: Double = 0.5, wait: Long = 100L) {
        appendMessageToLog(LogTag.INFO, "Поиск максимального момента...")

        val initTime = System.currentTimeMillis()
        val initValue = testModel.measuredTorque
        val initPercent = 10
        val timeout = 30000

        while (isRunning && testModel.measuredTorque > testModel.measuredTorqueMax) {
            testModel.measuredTorqueMax = testModel.measuredTorque
            if ((abs(testModel.measuredTorque - initValue) / initValue) < initPercent / 100.0) {
                val elapsedTime = System.currentTimeMillis() - initTime
                if (elapsedTime > timeout) {
                    cause = "в течение ${timeout / 1000} секунд значение изменилось меньше, чем на $initPercent%"
                }
            }
            testModel.fLM -= step
            if (testModel.fLM <= 0) {
                testModel.fLM = 0.0
//                CM.device<C2000>(CM.DeviceID.UZ92).setObjectFOut(testModel.fLM)
                try {
                    showTwoWayDialog(
                        "Внимание",
                        "Достигнут предел регулирования(f min = 0 Гц). Продолжить опыт?",
                        "ДА",
                        "НЕТ",
                        { testModel.isContinueAgree = true },
                        { stop() },
                        200_000,
                        { false },
                        find(view).currentWindow!!
                    )
                } catch (e: Exception) {
                    appendMessageToLog(LogTag.DEBUG, "Диалог не был обработан в течение 200с")
                }
                return
            }
//            CM.device<C2000>(CM.DeviceID.UZ92).setObjectFOut(testModel.fLM)
            sleep(wait)
        }
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
        testModel.storedData.P2.value = testModel.measuredData.P2.value
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
        testModel.measuredData.P2.value = testModel.storedData.P2.value

        testModel.measuredData.torque.value = testModel.measuredTorqueMax.autoformat()
    }

    override fun saveProtocol() {
        reportFields["TEST_NAME_MAX_M"] = name

        reportFields["POWER"] = testModel.specifiedP.toString()
        reportFields["VOLTAGE_LIN"] = testModel.specifiedU.toString()
        reportFields["COS"] = testModel.specifiedCos.toString()
        reportFields["EFFICIENCY"] = testModel.specifiedEfficiency.toString()
        reportFields["AMPERAGE_PHASE"] = testModel.specifiedI.toString()
        reportFields["RPM"] = testModel.specifiedRPM.toString()
        reportFields["FREQ"] = testModel.specifiedF.toString()
        reportFields["SCHEME"] = testModel.specifiedScheme

        reportFields["U_MAX_M"] = testModel.measuredData.U.value
        reportFields["L1_U_MAX_M"] = testModel.measuredData.UAB.value
        reportFields["L2_U_MAX_M"] = testModel.measuredData.UBC.value
        reportFields["L3_U_MAX_M"] = testModel.measuredData.UCA.value
        reportFields["I_MAX_M"] = testModel.measuredData.I.value
        reportFields["L1_I_MAX_M"] = testModel.measuredData.IA.value
        reportFields["L2_I_MAX_M"] = testModel.measuredData.IB.value
        reportFields["L3_I_MAX_M"] = testModel.measuredData.IC.value
        reportFields["TOTAL_P_MAX_M"] = testModel.measuredData.P2.value
        reportFields["TOTAL_PF_MAX_M"] = testModel.measuredData.cos.value
        reportFields["RESULT_MAX_M"] = testModel.measuredData.result.value

        super.saveProtocol()
    }
}
