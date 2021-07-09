package ru.avem.stand.modules.r.tests

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.i.tests.Test
import ru.avem.stand.modules.i.views.TestViewModule
import ru.avem.stand.modules.i.views.showOKDialog
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.CM.DeviceID.DD2
import ru.avem.stand.modules.r.communication.model.devices.danfoss.Danfoss
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PRModel
import tornadofx.controlsfx.warningNotification
import tornadofx.runLater
import java.lang.Thread.sleep
import kotlin.experimental.and
import kotlin.reflect.KClass


abstract class KSPADTest(
    view: KClass<out TestViewModule>,
    reportTemplate: String,
    isNeedToSaveProtocol: Boolean = true
) : Test(view, reportTemplate, isNeedToSaveProtocol) {
    @Volatile
    protected var isStartPressed: Boolean = false

    val CURRENT_STAGE_PM130 = 200 / 5
    val CURRENT_STAGE_PM130_VIU = 1 / 5
    val COEF_TR_AVEM = 10000 / 100
    val COEF_SHUNT_PV27_PV28 = 1000 / 150


    val COEF_SHUNT_PV24 = 1000 / 1


    override fun initVars() {
        super.initVars()

        testModel.testItemData.U_Y_MPT.value = PreFillModel.testTypeProp.value.fields["U_Y_MPT"]?.value
        testModel.testItemData.I_Y_MPT.value = PreFillModel.testTypeProp.value.fields["I_Y_MPT"]?.value
        testModel.testItemData.U_V_MPT.value = PreFillModel.testTypeProp.value.fields["U_V_MPT"]?.value
        testModel.testItemData.I_V_MPT.value = PreFillModel.testTypeProp.value.fields["I_V_MPT"]?.value
        testModel.testItemData.R_IKAS_MPT.value = PreFillModel.testTypeProp.value.fields["R_IKAS_MPT"]?.value
        testModel.testItemData.R_MGR_MPT.value = PreFillModel.testTypeProp.value.fields["R_MGR_MPT"]?.value
        testModel.testItemData.U_HV_MPT.value = PreFillModel.testTypeProp.value.fields["U_HV_MPT"]?.value
        testModel.testItemData.U_MGR_MPT.value = PreFillModel.testTypeProp.value.fields["U_MGR_MPT"]?.value
        testModel.testItemData.I_HV_MPT.value = PreFillModel.testTypeProp.value.fields["I_HV_MPT"]?.value
        testModel.testItemData.T_HV_MPT.value = PreFillModel.testTypeProp.value.fields["T_HV_MPT"]?.value
        testModel.testItemData.U_Y_SG.value = PreFillModel.testTypeProp.value.fields["U_Y_SG"]?.value
        testModel.testItemData.U_V_SG.value = PreFillModel.testTypeProp.value.fields["U_V_SG"]?.value
        testModel.testItemData.R_IKAS_SG.value = PreFillModel.testTypeProp.value.fields["R_IKAS_SG"]?.value
        testModel.testItemData.R_MGR_SG.value = PreFillModel.testTypeProp.value.fields["R_MGR_SG"]?.value
        testModel.testItemData.U_HV_SG.value = PreFillModel.testTypeProp.value.fields["U_HV_SG"]?.value
        testModel.testItemData.U_MGR_SG.value = PreFillModel.testTypeProp.value.fields["U_MGR_SG"]?.value
        testModel.testItemData.I_HV_SG.value = PreFillModel.testTypeProp.value.fields["I_HV_SG"]?.value
        testModel.testItemData.T_HV_SG.value = PreFillModel.testTypeProp.value.fields["T_HV_SG"]?.value
        testModel.testItemData.IDLE_TIME.value = PreFillModel.testTypeProp.value.fields["IDLE_TIME"]?.value
        testModel.testItemData.LOAD_TIME.value = PreFillModel.testTypeProp.value.fields["LOAD_TIME"]?.value

        testModel.protections.resetAll()
    }

    override fun initDevices() {
        super.initDevices()

        addCheckableDevice(DD2)
        CM.addWritingRegister(
            DD2,
            PRModel.CMD,
            1.toShort()
        )
        CM.device<PR>(DD2).init()
    }

    fun startPollControlUnit() {
        if (isRunning) {
            CM.startPoll(DD2, PRModel.DI_01_16_TRIG) { value ->
                val isStopPressed = value.toShort() and 0b1 != 0.toShort()
                if (isStopPressed) stop()

                isStartPressed = value.toShort() and 0b10 != 0.toShort()
            }
            CM.startPoll(DD2, PRModel.DI_01_16_TRIG_INV) { value ->
                if (value.toShort() and 0b100 != 0.toShort()) {
                    cause = "открыта дверь ШСО"
                    testModel.protections.doorsPEC.set()
                }
                if (value.toShort() and 0b1000 != 0.toShort()) {
                    cause = "сработала токовая защита ОИ"
                    testModel.protections.overcurrentTI.set()
                }
                if (value.toShort() and 0b10000 != 0.toShort()) {
                    cause = "сработала токовая защита ВИУ"
                    testModel.protections.overcurrentHV.set()
                    CM.device<PR>(DD2).offPEQV3()
                    CM.device<PR>(DD2).offVIUQV1()
                }
                if (value.toShort() and 0b100000 != 0.toShort()) {
                    cause = "открыта дверь зоны"
                    testModel.protections.doorZone.set()
                }
                if (value.toShort() and 0b1000000 != 0.toShort()) {
//                    cause = "Замкнут PE"
                    testModel.protections.PE.set()
                }
                if (value.toShort() and 0b10000000 == 0.toShort()) {
//                    cause = "PE не снят"
                    testModel.protections.notPE.set()
                }
                if (value.toShort() and 0b100000000 == 0.toShort()) {
//                    cause =  "открыт контактный пост"
                    testModel.protections.contactPosts.reset() //TODO контактный пост
                }
            }
        }
    }

    override fun prepare() {
        if (isRunning) {
            initPushButtonPost()
        }
//        if (isRunning) {
//            appendMessageToLog(LogTag.INFO, "Сигнализация")
//            CM.device<PR>(DD2).signalize()
//        }
    }

    private fun initPushButtonPost() {
        isStartPressed = false
        sleep(2000)
        if (isRunning && !isStartPressed) {
            appendMessageToLog(LogTag.WARN, "Убедитесь в правильности подключений крокодилов и нажмите кнопку ПУСК")
            runLater {
                warningNotification("Внимание", "Убедитесь в правильности подключений крокодилов и нажмите кнопку ПУСК")
            }
        }

        var timeout = 300 // 30c
        while (isRunning && !isStartPressed && timeout-- > 0) {
            sleep(100)
        }

        if (isRunning && !isStartPressed) {
            cause = "не была нажата кнопка ПУСК в течение 30 секунд"
        } else {
            CM.device<PR>(DD2).resetTriggers()
            sleep(1000)
        }
    }

    protected fun waitUntilFIToLoad() {
        appendMessageToLog(LogTag.INFO, "Загрузка ЧП...")
        sleepWhileRun(8)
    }

    fun startFI(volt: Int, percent: Int) {
        appendMessageToLog(LogTag.INFO, "Разгон ЧП...")
        CM.device<Danfoss>(CM.DeviceID.UZ91).setObjectParams(
            volt = volt/*volt / 8.42 / 1.4 * 1.2*/,
            perc = percent
        )
        sleep(1000)
        CM.device<Danfoss>(CM.DeviceID.UZ91).startObject()
    }

    protected fun waitUntilFIToRun(accTime: Int = 10) {
        appendMessageToLog(LogTag.INFO, "Ожидание разгона...")
        sleepWhileRun(accTime)
    }

    protected fun stopFI(fi: Danfoss, stopTime: Int = 10) {
        appendMessageToLog(LogTag.INFO, "Останов...")
        fi.stopObject()
        sleep(1000.toLong() * stopTime)
    }

    private fun waitUntilFIToStop(stopTime: Int = 10) {
        appendMessageToLog(LogTag.INFO, "Ожидание останова...")
        sleep(stopTime * 1000.toLong())
    }

    override fun finalizeDevices() {
        CM.device<PR>(DD2).offAllKMs()
        super.finalizeDevices()

        testModel.protections.earthingSwitch.reset()
    }
}
