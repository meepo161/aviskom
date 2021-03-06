package ru.avem.stand.modules.r.tests.pi.overi

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.i.views.showTwoWayDialog
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.devices.danfoss.Danfoss
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.owen.trm202.TRM202
import ru.avem.stand.modules.r.communication.model.devices.owen.trm202.TRM202Model
import ru.avem.stand.modules.r.communication.model.devices.satec.pm130.PM130Model

import ru.avem.stand.modules.r.tests.KSPADTest
import ru.avem.stand.modules.r.tests.calcSyncRPM
import ru.avem.stand.modules.r.tests.calcZs
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.find
import tornadofx.runLater
import java.lang.Thread.sleep
import kotlin.collections.set
import kotlin.math.abs

class OverI : KSPADTest(view = OverIView::class, reportTemplate = "overi.xlsx") {
    override val name = "Испытание перегрузки по току"

    override val testModel = OverIModel

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

        testModel.specifiedM =
            PreFillModel.testTypeProp.value.fields["TORQUE"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedOverIRatio =
            PreFillModel.testTypeProp.value.fields["OVER_I_RATIO"]?.value.toDoubleOrDefault(1.0)
        testModel.specifiedOverITestTime =
            PreFillModel.testTypeProp.value.fields["OVER_I_TIME"]?.value.toDoubleOrDefault(0.0)

        testModel.isContinueAgree = false
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

            testModel.measuredData.T.value = ""
            testModel.measuredData.torque.value = ""
            testModel.measuredData.RPM.value = ""
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

        if (isRunning) {
            with(CM.device<TRM202>(CM.DeviceID.PS81)) {
                with(getRegisterById(TRM202Model.T_1)) {
                    readRegister(this)
                    testModel.measuredData.T.value = value.toDouble().autoformat()
                }
            }
        }
    }

    override fun logic() {
//        calcF()
//        if (isRunning) {
//            turnOnCircuit()
//        }
//        if (isRunning) {
//            waitUntilFIToLoad()
//        }
//        if (isRunning) {
//            checkLMDirection()
//        }
//        if (isRunning) {
//            startTIFI()
//            waitUntilFIToRun()
//        }
//        if (isRunning) {
//            startLMFI()
//        }
//        if (isRunning) {
//            load()
//        }
//        if (isRunning) {
//            selectAmperageStage()
//        }
//        if (isRunning) {
//            waiting()
//        }
//        storeTestValues()
//        if (isRunning) {
//            CM.device<PR>(CM.DeviceID.DD2).offShuntirGB30()
//            returnAmperageStage()
//            stopFI(CM.device(CM.DeviceID.UZ91))
//        }
    }

    private fun calcF() {
        val (zTI, zLM) = calcZs(testModel.syncRPM.toInt())
        showZDialog(zTI.toInt(), zLM.toInt())
        val nTI = testModel.specifiedRPM // TODO реальное значение
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
        CM.device<PR>(CM.DeviceID.DD2).onShuntirGB30()
        CM.device<PR>(CM.DeviceID.DD2).onPEQV3()
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
        testModel.isLMDirectionRight = testModel.measuredData.torque.value.toDouble() > 0.0
//        CM.device<C2000>(CM.DeviceID.UZ92).stopObject()
        sleepWhileRun(10)
    }

    private fun startTIFI() {
        appendMessageToLog(LogTag.INFO, "Разгон ОИ...")
        CM.device<Danfoss>(CM.DeviceID.UZ91).setObjectParams(
            volt = 100,
            perc= 100,
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
        testModel.isTIDirectionRight = testModel.measuredData.torque.value.toDouble() < 0.0
        val lmDirection = if (!testModel.isLMDirectionRight xor !testModel.isTIDirectionRight) {
            appendMessageToLog(LogTag.INFO, "Реверс НМ")
            Danfoss.Direction.REVERSE
        } else {
            Danfoss.Direction.FORWARD
        }
//        CM.device<C2000>(CM.DeviceID.UZ92).startObject(lmDirection)

        while (isRunning && u < maxU) {
            u++
//            CM.device<C2000>(CM.DeviceID.UZ92).setObjectUMax(u)
            sleep(50)
        }

//        CM.device<C2000>(CM.DeviceID.UZ92).setObjectUMax(maxU)
    }

    private fun load() {
        appendMessageToLog(LogTag.INFO, "Нагрузка")

        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Грубая регулировка нагрузки")
            regulationTo(minPercent = 20.0, step = 0.05, wait = 750L)
        }

        if (isRunning) {
            selectAmperageStage()
        }

        if (isRunning && !testModel.isContinueAgree) {
            appendMessageToLog(LogTag.INFO, "Точная регулировка нагрузки")
            regulationTo(minPercent = 2.0, maxPercent = 2.0, step = 0.01, wait = 1250L)
        }
    }

    private fun regulationTo(minPercent: Double, maxPercent: Double = minPercent, step: Double, wait: Long) {
        val min = testModel.specifiedI * testModel.specifiedOverIRatio * (100.0 - minPercent) / 100.0
        val max = testModel.specifiedI * testModel.specifiedOverIRatio * (100.0 + maxPercent) / 100.0

        val initTime = System.currentTimeMillis()
        val initValue = testModel.measuredI
        val initPercent = 10
        val timeout = 30_000

        while (isRunning && (testModel.measuredI < min || testModel.measuredI > max)) {
            if ((abs(testModel.measuredI - initValue) / initValue) < initPercent / 100.0) {
                val elapsedTime = System.currentTimeMillis() - initTime
                if (elapsedTime > timeout) {
                    cause = "в течение ${timeout / 1000} секунд значение изменилось меньше, чем на $initPercent%"
                }
            }
            if (testModel.measuredI < min) {
                testModel.fLM -= step
            }
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
            if (testModel.measuredI > max) {
                testModel.fLM += step
            }
            if (testModel.fLM >= 55.0) {
                testModel.fLM = 55.0
//                CM.device<C2000>(CM.DeviceID.UZ92).setObjectFOut(testModel.fLM)
                try {
                    showTwoWayDialog(
                        "Внимание",
                        "Достигнут предел регулирования(f max = 55 Гц). Продолжить опыт?",
                        "ДА",
                        "НЕТ",
                        { },
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

    private fun selectAmperageStage() {
        appendMessageToLog(LogTag.INFO, "Подбор токовой ступени...")
        if (isRunning && testModel.measuredI < 30) {
            appendMessageToLog(LogTag.INFO, "Переключение на 30/5")
//            CM.device<PR>(CM.DeviceID.DD2).on30To5AmperageStage()
//            CM.device<PR>(CM.DeviceID.DD2).offMaxAmperageStage()
            
            sleepWhileRun(3)
            if (isRunning && testModel.measuredI < 4) {
                appendMessageToLog(LogTag.INFO, "Переключение на 5/5")
//                CM.device<PR>(CM.DeviceID.DD2).onMinAmperageStage()
//                CM.device<PR>(CM.DeviceID.DD2).off30To5AmperageStage()
                
            }
        }
    }

    private fun waiting() {
        appendMessageToLog(LogTag.INFO, "Ожидание...")
        sleepWhileRun(testModel.specifiedOverITestTime.toInt(), progressProperty = testModel.progressProperty)
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
    }

    private fun returnAmperageStage() {
        appendMessageToLog(LogTag.INFO, "Возврат токовой ступени...")
//        CM.device<PR>(CM.DeviceID.DD2).onMaxAmperageStage()
        
//        CM.device<PR>(CM.DeviceID.DD2).offOtherAmperageStages()
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
    }

    override fun saveProtocol() {
        reportFields["TEST_NAME_OVER_I"] = name

        reportFields["POWER"] = testModel.specifiedP.toString()
        reportFields["VOLTAGE_LIN"] = testModel.specifiedU.toString()
        reportFields["COS"] = testModel.specifiedCos.toString()
        reportFields["EFFICIENCY"] = testModel.specifiedEfficiency.toString()
        reportFields["AMPERAGE_PHASE"] = testModel.specifiedI.toString()
        reportFields["RPM"] = testModel.specifiedRPM.toString()
        reportFields["FREQ"] = testModel.specifiedF.toString()
        reportFields["SCHEME"] = testModel.specifiedScheme

        reportFields["U_OVER_I"] = testModel.measuredData.U.value
        reportFields["L1_U_OVER_I"] = testModel.measuredData.UAB.value
        reportFields["L2_U_OVER_I"] = testModel.measuredData.UBC.value
        reportFields["L3_U_OVER_I"] = testModel.measuredData.UCA.value
        reportFields["I_OVER_I"] = testModel.measuredData.I.value
        reportFields["L1_I_OVER_I"] = testModel.measuredData.IA.value
        reportFields["L2_I_OVER_I"] = testModel.measuredData.IB.value
        reportFields["L3_I_OVER_I"] = testModel.measuredData.IC.value
        reportFields["TOTAL_PF_OVER_I"] = testModel.measuredData.cos.value
        reportFields["TEMP_OVER_I"] = testModel.measuredData.T.value
        reportFields["RESULT_OVER_I"] = testModel.measuredData.result.value

        super.saveProtocol()
    }
}
