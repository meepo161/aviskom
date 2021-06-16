package ru.avem.stand.modules.r.tests.pi.minm

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.i.views.showTwoWayDialog
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.devices.danfoss.Danfoss
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.satec.pm130.PM130Model

import ru.avem.stand.modules.r.tests.KSPADTest
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.find
import tornadofx.runLater
import java.lang.Thread.sleep
import kotlin.collections.set
import kotlin.math.abs

class MinM : KSPADTest(view = MinMView::class, reportTemplate = "minm.xlsx") {
    override val name = "Определение минимального вращающего момента в процессе пуска"

    override val testModel = MinMModel

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

            testModel.measuredData.result.value = ""

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
            }
        }
    }

    override fun logic() {
        if (isRunning) {
            turnOnCircuit()
        }
        if (isRunning) {
            waitUntilFIToLoad()
        }
        if (isRunning) {
            checkLMDirectionsOfRotation()
        }
        if (isRunning) {
            findMinM()
        }
        if (isRunning) {
            CM.device<PR>(CM.DeviceID.DD2).offPEQV3()
//            stopFI(CM.device(CM.DeviceID.UZ92))
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

    private fun checkLMDirectionsOfRotation() {
        testModel.isNeedAbsTorque = false
        if (isRunning) {
            checkLMDirection()
        }
        if (isRunning) {
            checkTIDirection()
        }
        testModel.isNeedAbsTorque = true
    }

    private fun checkLMDirection() {
        appendMessageToLog(LogTag.INFO, "Проверка направления вращения НМ...")

        CM.device<PR>(CM.DeviceID.DD2).onShuntirGB30()

//        CM.device<C2000>(CM.DeviceID.UZ92).setObjectParams(
//            fOut = 50,
//
//            voltageP1 = 380,
//            fP1 = 50,
//
//            voltageP2 = 1,
//            fP2 = 1
//        )
//        startFI(CM.device(CM.DeviceID.UZ92), 5)
        testModel.isLMDirectionRight = testModel.measuredData.torque.value.toDouble() > 0.0
//        stopFI(CM.device(CM.DeviceID.UZ92))

        CM.device<PR>(CM.DeviceID.DD2).offShuntirGB30()
    }

    private fun checkTIDirection() {
        appendMessageToLog(LogTag.INFO, "Проверка направления вращения ОИ...")

        CM.device<PR>(CM.DeviceID.DD2).onPEQV3()

        CM.device<Danfoss>(CM.DeviceID.UZ91).setObjectParams(
            voltage = 100,
            percentF = 100,
        )
//        startFI(CM.device(CM.DeviceID.UZ91), 5) // TODO остальные checkDir
        testModel.isTIDirectionRight = testModel.measuredData.torque.value.toDouble() < 0.0
        stopFI(CM.device(CM.DeviceID.UZ91))

        CM.device<PR>(CM.DeviceID.DD2).offPEQV3()
    }

    private fun findMinM() {
        appendMessageToLog(LogTag.INFO, "Поиск минимального момента")

        var loadMachineMinU = 0.0
        var loadMachineMaxU = 380.0
        val maxAttempts = 10
        var attempts = maxAttempts
        var loadMachineCurU = loadMachineMinU + (loadMachineMaxU - loadMachineMinU) / 2.0
        testModel.result = MinMModel.ResultBlob.UNDEFINED

        while (isRunning && (attempts-- > 0)) {
            if (testModel.result == MinMModel.ResultBlob.NEED_LESS) {
                loadMachineMaxU = loadMachineCurU
            } else if (testModel.result == MinMModel.ResultBlob.NEED_MORE) {
                loadMachineMinU = loadMachineCurU
            }
            loadMachineCurU = loadMachineMinU + (loadMachineMaxU - loadMachineMinU) / 2.0
            checkIteration(loadMachineCurU)
            if (testModel.result != MinMModel.ResultBlob.FOUND) {
                try {
                    showTwoWayDialog(
                        "Внимание",
                        "Объект испытания переосилил нагрузочную машину?",
                        "ДА",
                        "НЕТ",
                        { testModel.result = MinMModel.ResultBlob.NEED_LESS },
                        { testModel.result = MinMModel.ResultBlob.NEED_MORE },
                        200_000,
                        { false },
                        find(view).currentWindow!!
                    )
                } catch (e: Exception) {
                    appendMessageToLog(LogTag.DEBUG, "Диалог не был обработан в течение 200с")
                }
            } else {
                break
            }
        }
    }

    private fun checkIteration(u: Double) {
        appendMessageToLog(LogTag.INFO, "U = $u В")
        appendMessageToLog(LogTag.INFO, "Разгон НМ...")

        testModel.measuredTorqueMax = -1.0

        val lmDirection = if (!(!testModel.isLMDirectionRight xor !testModel.isTIDirectionRight)) {
            appendMessageToLog(LogTag.INFO, "Реверс НМ")
            Danfoss.Direction.REVERSE
        } else {
            Danfoss.Direction.FORWARD
        }
//        CM.device<C2000>(CM.DeviceID.UZ92).startObject(lmDirection)
        waitUntilFIToRun()

        if (isRunning) {
            startTIFI()
        }

        if (isRunning) {
            waiting()
        }

        if ((testModel.measuredData.RPM.value.toDouble() > (testModel.specifiedRPM * 0.2)) &&
            (testModel.measuredData.RPM.value.toDouble() < (testModel.specifiedRPM * 0.9))
        ) {
            testModel.result = MinMModel.ResultBlob.FOUND
            storeTestValues()
        }


        stopFI(CM.device(CM.DeviceID.UZ91))

//        stopFI(CM.device(CM.DeviceID.UZ92))

    }

    private fun startTIFI() {
        appendMessageToLog(LogTag.INFO, "Разгон ОИ...")

        CM.device<Danfoss>(CM.DeviceID.UZ91).setObjectParams(
            voltage = 100,
            percentF = 100,
        )
//        startFI(CM.device(CM.DeviceID.UZ91))
    }

    private fun waiting() {
        appendMessageToLog(LogTag.INFO, "Анализ данных...")
        sleepWhileRun(5, progressProperty = testModel.progressProperty)
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

        testModel.measuredTorqueMin = testModel.measuredTorqueMax
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

        if (isSuccess) {
            testModel.measuredData.torque.value = testModel.measuredTorqueMin.autoformat()
            appendMessageToLog(LogTag.INFO, "Mmin = ${testModel.measuredData.torque.value} Н⋅м")
        } else {
            testModel.measuredData.torque.value = Double.NaN.autoformat()
        }
    }

    override fun saveProtocol() {
        reportFields["TEST_NAME_MIN_M"] = name

        reportFields["POWER"] = testModel.specifiedP.toString()
        reportFields["VOLTAGE_LIN"] = testModel.specifiedU.toString()
        reportFields["COS"] = testModel.specifiedCos.toString()
        reportFields["EFFICIENCY"] = testModel.specifiedEfficiency.toString()
        reportFields["AMPERAGE_PHASE"] = testModel.specifiedI.toString()
        reportFields["RPM"] = testModel.specifiedRPM.toString()
        reportFields["FREQ"] = testModel.specifiedF.toString()
        reportFields["SCHEME"] = testModel.specifiedScheme

        reportFields["U_MIN_M"] = testModel.measuredData.U.value
        reportFields["L1_U_MIN_M"] = testModel.measuredData.UAB.value
        reportFields["L2_U_MIN_M"] = testModel.measuredData.UBC.value
        reportFields["L3_U_MIN_M"] = testModel.measuredData.UCA.value
        reportFields["I_MIN_M"] = testModel.measuredData.I.value
        reportFields["L1_I_MIN_M"] = testModel.measuredData.IA.value
        reportFields["L2_I_MIN_M"] = testModel.measuredData.IB.value
        reportFields["L3_I_MIN_M"] = testModel.measuredData.IC.value
        reportFields["RESULT_MIN_M"] = testModel.measuredData.result.value

        super.saveProtocol()
    }
}
