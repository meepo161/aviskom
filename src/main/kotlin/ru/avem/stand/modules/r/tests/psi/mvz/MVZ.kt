package ru.avem.stand.modules.r.tests.psi.mvz

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.CM.DeviceID.*
import ru.avem.stand.modules.r.communication.model.devices.danfoss.Danfoss
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.satec.pm130.PM130Model

import ru.avem.stand.modules.r.tests.KSPADTest
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.*
import java.lang.Thread.sleep
import kotlin.math.abs
import kotlin.math.sqrt

class MVZ : KSPADTest(view = MVZView::class, reportTemplate = "mvz.xlsx") {
    override val name = "Испытание электрической прочности междувитковой изоляции обмоток"

    override val testModel = MVZModel

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

        testModel.specifiedMVZTestTime = PreFillModel.testTypeProp.value.fields["MVZ_TIME"]!!.value.toDouble()

        testModel.tolerance = PreFillModel.testTypeProp.value.fields["MVZ_TOLERANCE"]?.value.toDoubleOrDefault(0.0)

        testModel.beforeFIP1U = 0.0
        testModel.lastFIP1U = 0.0
    }

    override fun initView() {
        super.initView()

        runLater {
            testModel.progressProperty.value = -1.0
            testModel.measuredDataBefore.U.value = ""
            testModel.measuredDataBefore.UAB.value = ""
            testModel.measuredDataBefore.UBC.value = ""
            testModel.measuredDataBefore.UCA.value = ""
            testModel.measuredDataBefore.I.value = ""
            testModel.measuredDataBefore.IA.value = ""
            testModel.measuredDataBefore.IB.value = ""
            testModel.measuredDataBefore.IC.value = ""
            testModel.measuredDataBefore.result.value = ""

            testModel.measuredDataDuring.U.value = ""
            testModel.measuredDataDuring.UAB.value = ""
            testModel.measuredDataDuring.UBC.value = ""
            testModel.measuredDataDuring.UCA.value = ""
            testModel.measuredDataDuring.I.value = ""
            testModel.measuredDataDuring.IA.value = ""
            testModel.measuredDataDuring.IB.value = ""
            testModel.measuredDataDuring.IC.value = ""
            testModel.measuredDataDuring.result.value = ""

            testModel.measuredDataAfter.U.value = ""
            testModel.measuredDataAfter.UAB.value = ""
            testModel.measuredDataAfter.UBC.value = ""
            testModel.measuredDataAfter.UCA.value = ""
            testModel.measuredDataAfter.I.value = ""
            testModel.measuredDataAfter.IA.value = ""
            testModel.measuredDataAfter.IB.value = ""
            testModel.measuredDataAfter.IC.value = ""
            testModel.measuredDataAfter.result.value = ""
        }
    }

    override fun startPollDevices() {
        super.startPollDevices()

        startPollControlUnit()

        if (isRunning) {
            with(PAV41) {
                addCheckableDevice(this)
                CM.startPoll(this, PM130Model.U_AB_REGISTER) { value ->
                    testModel.measuredUAB = value.toDouble()
                    testModel.measuredU = (testModel.measuredUAB + testModel.measuredUBC + testModel.measuredUCA) / 3
                    when (testModel.stage) {
                        MVZModel.Stage.BEFORE -> {
                            testModel.measuredDataBefore.UAB.value = value.toDouble().autoformat()
                            testModel.measuredDataBefore.U.value = testModel.measuredU.autoformat()
                        }
                        MVZModel.Stage.DURING -> {
                            testModel.measuredDataDuring.UAB.value = value.toDouble().autoformat()
                            testModel.measuredDataDuring.U.value = testModel.measuredU.autoformat()
                        }
                        MVZModel.Stage.AFTER -> {
                            testModel.measuredDataAfter.UAB.value = value.toDouble().autoformat()
                            testModel.measuredDataAfter.U.value = testModel.measuredU.autoformat()
                        }
                    }
                }
                CM.startPoll(this, PM130Model.U_BC_REGISTER) { value ->
                    testModel.measuredUBC = value.toDouble()
                    testModel.measuredU = (testModel.measuredUAB + testModel.measuredUBC + testModel.measuredUCA) / 3
                    when (testModel.stage) {
                        MVZModel.Stage.BEFORE -> {
                            testModel.measuredDataBefore.UBC.value = value.toDouble().autoformat()
                            testModel.measuredDataBefore.U.value = testModel.measuredU.autoformat()
                        }
                        MVZModel.Stage.DURING -> {
                            testModel.measuredDataDuring.UBC.value = value.toDouble().autoformat()
                            testModel.measuredDataDuring.U.value = testModel.measuredU.autoformat()
                        }
                        MVZModel.Stage.AFTER -> {
                            testModel.measuredDataAfter.UBC.value = value.toDouble().autoformat()
                            testModel.measuredDataAfter.U.value = testModel.measuredU.autoformat()
                        }
                    }
                }
                CM.startPoll(this, PM130Model.U_CA_REGISTER) { value ->
                    testModel.measuredUCA = value.toDouble()
                    testModel.measuredU = (testModel.measuredUAB + testModel.measuredUBC + testModel.measuredUCA) / 3
                    when (testModel.stage) {
                        MVZModel.Stage.BEFORE -> {
                            testModel.measuredDataBefore.UCA.value = value.toDouble().autoformat()
                            testModel.measuredDataBefore.U.value = testModel.measuredU.autoformat()
                        }
                        MVZModel.Stage.DURING -> {
                            testModel.measuredDataDuring.UCA.value = value.toDouble().autoformat()
                            testModel.measuredDataDuring.U.value = testModel.measuredU.autoformat()
                        }
                        MVZModel.Stage.AFTER -> {
                            testModel.measuredDataAfter.UCA.value = value.toDouble().autoformat()
                            testModel.measuredDataAfter.U.value = testModel.measuredU.autoformat()
                        }
                    }
                }

                CM.startPoll(this, PM130Model.I_A_REGISTER) { value ->
                    testModel.measuredIA = abs(value.toDouble() * CURRENT_STAGE_PM130)
                    testModel.measuredI = (testModel.measuredIA + testModel.measuredIB + testModel.measuredIC) / 3
                    when (testModel.stage) {
                        MVZModel.Stage.BEFORE -> {
                            testModel.measuredDataBefore.IA.value = testModel.measuredIA.autoformat()
                            testModel.measuredDataBefore.I.value = testModel.measuredI.autoformat()
                        }
                        MVZModel.Stage.DURING -> {
                            testModel.measuredDataDuring.IA.value = testModel.measuredIA.autoformat()
                            testModel.measuredDataDuring.I.value = testModel.measuredI.autoformat()
                        }
                        MVZModel.Stage.AFTER -> {
                            testModel.measuredDataAfter.IA.value = testModel.measuredIA.autoformat()
                            testModel.measuredDataAfter.I.value = testModel.measuredI.autoformat()
                        }
                    }
                }
                CM.startPoll(this, PM130Model.I_B_REGISTER) { value ->
                    testModel.measuredIB = abs(value.toDouble() * CURRENT_STAGE_PM130)
                    testModel.measuredI = (testModel.measuredIA + testModel.measuredIB + testModel.measuredIC) / 3
                    when (testModel.stage) {
                        MVZModel.Stage.BEFORE -> {
                            testModel.measuredDataBefore.IB.value = testModel.measuredIB.autoformat()
                            testModel.measuredDataBefore.I.value = testModel.measuredI.autoformat()
                        }
                        MVZModel.Stage.DURING -> {
                            testModel.measuredDataDuring.IB.value = testModel.measuredIB.autoformat()
                            testModel.measuredDataDuring.I.value = testModel.measuredI.autoformat()
                        }
                        MVZModel.Stage.AFTER -> {
                            testModel.measuredDataAfter.IB.value = testModel.measuredIB.autoformat()
                            testModel.measuredDataAfter.I.value = testModel.measuredI.autoformat()
                        }
                    }
                }
                CM.startPoll(this, PM130Model.I_C_REGISTER) { value ->
                    testModel.measuredIC = abs(value.toDouble() * CURRENT_STAGE_PM130)
                    testModel.measuredI = (testModel.measuredIA + testModel.measuredIB + testModel.measuredIC) / 3
                    when (testModel.stage) {
                        MVZModel.Stage.BEFORE -> {
                            testModel.measuredDataBefore.IC.value = testModel.measuredIC.autoformat()
                            testModel.measuredDataBefore.I.value = testModel.measuredI.autoformat()
                        }
                        MVZModel.Stage.DURING -> {
                            testModel.measuredDataDuring.IC.value = testModel.measuredIC.autoformat()
                            testModel.measuredDataDuring.I.value = testModel.measuredI.autoformat()
                        }
                        MVZModel.Stage.AFTER -> {
                            testModel.measuredDataAfter.IC.value = testModel.measuredIC.autoformat()
                            testModel.measuredDataAfter.I.value = testModel.measuredI.autoformat()
                        }
                    }
                }
                CM.startPoll(this, PM130Model.F_REGISTER) { value ->
                    testModel.measuredDataBefore.F.value = value.toDouble().autoformat()
                }
            }
        }
    }

    override fun logic() {
//        if (isRunning) {
//            toStage(MVZModel.Stage.BEFORE)
//        }
//        if (isRunning) {
//            turnOnCircuit()
//        }
//        if (isRunning) {
//            waitUntilFIToLoad()
//            startFI()
//            waitUntilFIToRun()
//        }
//        if (isRunning) {
//            appendMessageToLog(LogTag.INFO, "Точная регулировка до ${testModel.specifiedU}В")
//            regulateVoltage(specifiedU = testModel.specifiedU, minPercent = 0.0, maxPercent = 1.0, step = 0.1)
//            testModel.beforeFIP1U = testModel.lastFIP1U
//        }
//        if (isRunning) {
//            selectAmperageStage()
//        }
//        if (isRunning) {
//            toStage(MVZModel.Stage.DURING)
//        }
//        if (isRunning) {
//            returnAmperageStage()
//        }
//        if (isRunning) {
//            appendMessageToLog(LogTag.INFO, "Грубая регулировка до ${testModel.specifiedU * 1.3}В")
//            regulateVoltage(specifiedU = testModel.specifiedU * 1.3, minPercent = 3.0, step = 1.0)
//            appendMessageToLog(LogTag.INFO, "Точная регулировка до ${testModel.specifiedU * 1.3}В")
//            regulateVoltage(specifiedU = testModel.specifiedU * 1.3, minPercent = 0.0, maxPercent = 1.0, step = 0.1)
//        }
//        if (isRunning) {
//            selectAmperageStage()
//        }
//        if (isRunning) {
//            waiting()
//        }
//        if (isRunning) {
//            toStage(MVZModel.Stage.AFTER)
//        }
//        if (isRunning) {
//            returnAmperageStage()
//        }
//        if (isRunning) {
//            testModel.lastFIP1U = testModel.beforeFIP1U
//            CM.device<Danfoss>(UZ91).setObjectUMax(testModel.lastFIP1U)
//            sleepWhileRun(3)
//        }
//        if (isRunning) {
//            selectAmperageStage()
//        }
//        if (isRunning) {
//            sleepWhileRun(2)
//            storeTestValues()
//            returnAmperageStage()
//            stopFI(CM.device(UZ91))
//        }
    }

    private fun toStage(stage: MVZModel.Stage) {
        testModel.stage = stage
        sleep(1000)
    }

    private fun turnOnCircuit() {
        appendMessageToLog(LogTag.INFO, "Сбор схемы")
        CM.device<PR>(DD2).onIkasKM61()
        sleep(200)
//        CM.device<PR>(DD2).onMaxAmperageStage()
        
        sleep(200)
//        CM.device<PR>(DD2).onVoltageBoost()
        sleep(200)
//        CM.device<PR>(DD2).onMVZ()
        sleep(200)
            CM.device<PR>(DD2).onPEQV3()
        sleep(200)
    }

    fun startFi() {
        appendMessageToLog(LogTag.INFO, "Разгон ЧП...")
        testModel.lastFIP1U = (testModel.specifiedU / ((220.0 + 80.0) * sqrt(3.0) / 380.0)) * 0.95 //%
        CM.device<Danfoss>(UZ91).setObjectParams(
            volt = 100,
            perc = 100,
        )
        CM.device<Danfoss>(UZ91).startObject()
    }

    private fun regulateVoltage(
        specifiedU: Double,
        minPercent: Double,
        maxPercent: Double = minPercent,
        step: Double,
        wait: Long = 400L
    ) {
        val min = specifiedU * (100.0 - minPercent) / 100.0
        val max = specifiedU * (100.0 + maxPercent) / 100.0

        while (isRunning && (testModel.measuredU < min || testModel.measuredU > max)) {
            if (testModel.measuredU < min) {
                testModel.lastFIP1U += step
            }
            if (testModel.measuredU > max) {
                testModel.lastFIP1U -= step
            }
            CM.device<Danfoss>(UZ91).setObjectUMax(testModel.lastFIP1U)
            sleep(wait)
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

    private fun returnAmperageStage() {
        appendMessageToLog(LogTag.INFO, "Возврат токовой ступени...")
//        CM.device<PR>(DD2).onMaxAmperageStage()
        
//        CM.device<PR>(DD2).offOtherAmperageStages()
    }

    private fun waiting() {
        appendMessageToLog(LogTag.INFO, "Ожидание...")
        sleepWhileRun(testModel.specifiedMVZTestTime.toInt(), progressProperty = testModel.progressProperty)
    }

    private fun storeTestValues() {
        testModel.storedDataAfter.U.value = testModel.measuredDataAfter.U.value
        testModel.storedDataAfter.UAB.value = testModel.measuredDataAfter.UAB.value
        testModel.storedDataAfter.UBC.value = testModel.measuredDataAfter.UBC.value
        testModel.storedDataAfter.UCA.value = testModel.measuredDataAfter.UCA.value

        testModel.storedDataAfter.I.value = testModel.measuredDataAfter.I.value
        testModel.storedDataAfter.IA.value = testModel.measuredDataAfter.IA.value
        testModel.storedDataAfter.IB.value = testModel.measuredDataAfter.IB.value
        testModel.storedDataAfter.IC.value = testModel.measuredDataAfter.IC.value
    }

    override fun result() {
        super.result()

        if (!isSuccess) {
            testModel.measuredDataBefore.result.value = "Прервано"
            testModel.measuredDataDuring.result.value = "Прервано"
            testModel.measuredDataAfter.result.value = "Прервано"
            appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
        } else {
            testModel.diffIA =
                abs(testModel.measuredDataBefore.IA.value.toDouble() - testModel.storedDataAfter.IA.value.toDouble()) /
                        testModel.measuredDataBefore.IA.value.toDouble() * 100.0
            testModel.measuredDataAfter.diffIA.value = testModel.diffIA.autoformat()

            testModel.diffIB =
                abs(testModel.measuredDataBefore.IB.value.toDouble() - testModel.storedDataAfter.IB.value.toDouble()) /
                        testModel.measuredDataBefore.IB.value.toDouble() * 100.0
            testModel.measuredDataAfter.diffIB.value = testModel.diffIB.autoformat()

            testModel.diffIC =
                abs(testModel.measuredDataBefore.IC.value.toDouble() - testModel.storedDataAfter.IC.value.toDouble()) /
                        testModel.measuredDataBefore.IC.value.toDouble() * 100.0
            testModel.measuredDataAfter.diffIC.value = testModel.diffIC.autoformat()

            if (testModel.diffIA > testModel.tolerance || testModel.diffIB > testModel.tolerance || testModel.diffIC > testModel.tolerance) {
                testModel.measuredDataBefore.result.value = "Не соответствует"
                testModel.measuredDataDuring.result.value = "Не соответствует"
                testModel.measuredDataAfter.result.value = "Не соответствует"
                appendMessageToLog(
                    LogTag.ERROR,
                    "Результат: Разница между начальным и конечным токами больше, чем ${testModel.tolerance.autoformat()}%"
                )
            } else {
                testModel.measuredDataBefore.result.value = "Соответствует"
                testModel.measuredDataDuring.result.value = "Соответствует"
                testModel.measuredDataAfter.result.value = "Соответствует"
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

    private fun restoreTestValues() {
        testModel.measuredDataAfter.U.value = testModel.storedDataAfter.U.value
        testModel.measuredDataAfter.UAB.value = testModel.storedDataAfter.UAB.value
        testModel.measuredDataAfter.UBC.value = testModel.storedDataAfter.UBC.value
        testModel.measuredDataAfter.UCA.value = testModel.storedDataAfter.UCA.value

        testModel.measuredDataAfter.I.value = testModel.storedDataAfter.I.value
        testModel.measuredDataAfter.IA.value = testModel.storedDataAfter.IA.value
        testModel.measuredDataAfter.IB.value = testModel.storedDataAfter.IB.value
        testModel.measuredDataAfter.IC.value = testModel.storedDataAfter.IC.value
    }

    override fun saveProtocol() {
        reportFields["TEST_NAME_MVZ"] = name

        reportFields["POWER"] = testModel.specifiedP.toString()
        reportFields["VOLTAGE_LIN"] = testModel.specifiedU.toString()
        reportFields["COS"] = testModel.specifiedCos.toString()
        reportFields["EFFICIENCY"] = testModel.specifiedEfficiency.toString()
        reportFields["AMPERAGE_PHASE"] = testModel.specifiedI.toString()
        reportFields["RPM"] = testModel.specifiedRPM.toString()
        reportFields["FREQ"] = testModel.specifiedF.toString()
        reportFields["SCHEME"] = testModel.specifiedScheme

        reportFields["U_BEFORE_MVZ"] = testModel.measuredDataBefore.U.value
        reportFields["U_AB_BEFORE_MVZ"] = testModel.measuredDataBefore.UAB.value
        reportFields["U_BC_BEFORE_MVZ"] = testModel.measuredDataBefore.UBC.value
        reportFields["U_CA_BEFORE_MVZ"] = testModel.measuredDataBefore.UCA.value
        reportFields["I_BEFORE_MVZ"] = testModel.measuredDataBefore.I.value
        reportFields["I_A_BEFORE_MVZ"] = testModel.measuredDataBefore.IA.value
        reportFields["I_B_BEFORE_MVZ"] = testModel.measuredDataBefore.IB.value
        reportFields["I_C_BEFORE_MVZ"] = testModel.measuredDataBefore.IC.value
        reportFields["U_DURING_MVZ"] = testModel.measuredDataDuring.U.value
        reportFields["U_AB_DURING_MVZ"] = testModel.measuredDataDuring.UAB.value
        reportFields["U_BC_DURING_MVZ"] = testModel.measuredDataDuring.UBC.value
        reportFields["U_CA_DURING_MVZ"] = testModel.measuredDataDuring.UCA.value
        reportFields["I_DURING_MVZ"] = testModel.measuredDataDuring.I.value
        reportFields["I_A_DURING_MVZ"] = testModel.measuredDataDuring.IA.value
        reportFields["I_B_DURING_MVZ"] = testModel.measuredDataDuring.IB.value
        reportFields["I_C_DURING_MVZ"] = testModel.measuredDataDuring.IC.value
        reportFields["U_AFTER_MVZ"] = testModel.measuredDataAfter.U.value
        reportFields["U_AB_AFTER_MVZ"] = testModel.measuredDataAfter.UAB.value
        reportFields["U_BC_AFTER_MVZ"] = testModel.measuredDataAfter.UBC.value
        reportFields["U_CA_AFTER_MVZ"] = testModel.measuredDataAfter.UCA.value
        reportFields["I_AFTER_MVZ"] = testModel.measuredDataAfter.I.value
        reportFields["I_A_AFTER_MVZ"] = testModel.measuredDataAfter.IA.value
        reportFields["I_B_AFTER_MVZ"] = testModel.measuredDataAfter.IB.value
        reportFields["I_C_AFTER_MVZ"] = testModel.measuredDataAfter.IC.value

        reportFields["DIFF_I_A_MVZ"] = testModel.measuredDataAfter.diffIA.value
        reportFields["DIFF_I_B_MVZ"] = testModel.measuredDataAfter.diffIB.value
        reportFields["DIFF_I_C_MVZ"] = testModel.measuredDataAfter.diffIC.value

        reportFields["RESULT_MVZ"] = testModel.measuredDataAfter.result.value

        super.saveProtocol()
    }
}
