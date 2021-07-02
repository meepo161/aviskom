package ru.avem.stand.modules.i.tests

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.property.DoubleProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.WritableImage
import javafx.scene.text.Text
import ru.avem.stand.modules.i.Module
import ru.avem.stand.modules.i.views.TestViewModule
import ru.avem.stand.modules.i.views.showOKDialog
import ru.avem.stand.modules.i.views.showTwoWayDialog
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.CM.device
import ru.avem.stand.modules.r.communication.model.IDeviceController
import ru.avem.stand.modules.r.storage.Properties
import ru.avem.stand.modules.r.storage.database.saveProtocol
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.*
import java.io.File
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import javax.imageio.ImageIO
import kotlin.concurrent.thread
import kotlin.reflect.KClass

abstract class Test(
    val view: KClass<out TestViewModule>,
    val reportTemplate: String,
    private val isNeedToSaveProtocol: Boolean = true
) : Module() {
    companion object {
        var count = 0
    }

    val order = ++count

    abstract val name: String

    @Volatile
    protected var isRunning = false

    @Volatile
    var isFinished: Boolean = true

    @Volatile
    protected var cause: String = ""
        set(value) {
            if (value.isNotEmpty()) {
                isRunning = false
                if (!field.contains(value)) field += "${if (field != "") "/" else ""}$value"
            } else {
                field = value
            }
        }

    val isSuccess: Boolean
        get() = cause.isEmpty()

    private val checkableDevices: MutableList<IDeviceController> = mutableListOf()

    val reportFields: MutableMap<String, String> = mutableMapOf()

    fun appendOneMessageToLog(tag: LogTag, msg: String) {
        val lastText = find(view).vBoxLog.children.lastOrNull()
        if (lastText != null) {
            if (!((lastText as Text).text).contains(msg)) {
                appendMessageToLog(tag, msg)
            }
        } else {
            appendMessageToLog(tag, msg)
        }
    }

    protected open fun appendMessageToLog(tag: LogTag, msg: String) {
        runLater {
            find(view).vBoxLog.add(
                Text("${SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())} | $msg").apply {
                    style {
                        fill = tag.c
                    }
                }
            )
        }
    }

    open fun start() {
        thread(isDaemon = true) {
//            snapshot()
            init()
            if (isRunning) {
                startPollDevices()
            }

            if (isRunning) {
                prepare()
            }
            logic()
            result()

            finalize()

            if (isNeedToSaveProtocol) {
                saveProtocol()
            }
        }
    }

    private fun snapshot() {
        runLater {
            ImageIO.write(
                SwingFXUtils.fromFXImage(
                    find(view).modalStage?.scene?.snapshot(
                        WritableImage(
                            find(view).modalStage?.scene?.width?.toInt() ?: 0,
                            find(view).modalStage?.scene?.height?.toInt() ?: 0
                        )
                    ),
                    null
                ),
                "png",
                File("${name.replace("/", "")}.png")
            )
        }
    }

    protected open fun init() {
        initVars()

        if (isRunning) {
            initView()
            initDevices()
        }
//        createScreenShot(name)
    }

    protected open fun initVars() {
        cause = ""
        isRunning = true
        isFinished = false
    }

    protected open fun initView() {
        runLater {
            clearLog()
            find(view).cancelAllTestsButton.isVisible = false

            find(view).stopReloadButton.text = "Стоп"
            find(view).stopReloadButton.graphic = MaterialDesignIconView(MaterialDesignIcon.STOP).apply {
                glyphSize = 45
                fill = c("red")
            }

            find(view).nextTestButton.isVisible = false
        }
    }

    private fun clearLog() {
        find(view).vBoxLog.clear()
    }

    protected open fun initDevices() {
        appendMessageToLog(LogTag.INFO, "Инициализация приборов")

        thread(isDaemon = true) {
            while (isRunning) {
                val list = CM.listOfUnresponsiveDevices(checkableDevices)
                if (list.isNotEmpty()) {
                    cause = "следующие приборы не отвечают на запросы: $list"
                }
                sleep(100)
            }
        }
    }

    protected fun addCheckableDevice(id: CM.DeviceID) {
        with(device<IDeviceController>(id)) {
            checkResponsibility()
            checkableDevices.add(this)
        }
    }

    protected fun removeCheckableDevice(id: CM.DeviceID) {
        checkableDevices.remove(device(id))
    }

    open fun startPollDevices() {

    }

    protected open fun prepare() {

    }

    abstract fun logic()

    protected fun sleepWhileRun(
        timeSecond: Int,
        progressProperty: DoubleProperty? = null,
        isNeedContinue: () -> Boolean = { true }
    ) {
        var timer = timeSecond * 10
        while (isRunning && timer-- > 0 && isNeedContinue()) {
            if (timer % 10 == 0) {
                runLater {
                    progressProperty?.value = 1.0 - (timer / 10.0) / timeSecond
                }
            }
            sleep(100)
        }
        if (isNeedContinue()) {
            runLater {
                progressProperty?.value = -1.0
            }
        }
    }

    protected open fun result() {

    }

    private fun finalize() {
        finalizeDevices()
        finalizeView()
        finalizeVars()
    }

    open fun finalizeDevices() {
        checkableDevices.clear()
        CM.clearPollingRegisters()
    }

    open fun finalizeView() {
        runLater {
            find(view).cancelAllTestsButton.isVisible = true

            find(view).stopReloadButton.text = "Повторить"
            find(view).stopReloadButton.graphic = MaterialDesignIconView(MaterialDesignIcon.RELOAD).apply {
                glyphSize = 45
                fill = c("black")
            }

            find(view).nextTestButton.isVisible = true
        }
    }

    private fun finalizeVars() {
        isFinished = true
        isRunning = false
    }

    open fun saveProtocol() {
        reportFields["VOLTAGE_MPT_Y"] = PreFillModel.testTypeProp.value.fields["U_Y_MPT"]?.value.toDoubleOrDefault(0.0).toString()
        reportFields["AMPERAGE_MPT_Y"] = PreFillModel.testTypeProp.value.fields["I_Y_MPT"]?.value.toDoubleOrDefault(0.0).toString()
        reportFields["VOLTAGE_SG_V"] = PreFillModel.testTypeProp.value.fields["U_V_SG"]?.value.toDoubleOrDefault(0.0).toString()
        reportFields["VOLTAGE_SG_Y"] = PreFillModel.testTypeProp.value.fields["U_Y_SG"]?.value.toDoubleOrDefault(0.0).toString()

        reportFields["U_SPEC_MGR_MPT"] = PreFillModel.testTypeProp.value.fields["U_MGR_MPT"]?.value.toDoubleOrDefault(0.0).toString()
        reportFields["R_SPEC_MGR_MPT"] = PreFillModel.testTypeProp.value.fields["R_MGR_MPT"]?.value.toDoubleOrDefault(0.0).toString()
        reportFields["U_SPEC_MGR_SG"] = PreFillModel.testTypeProp.value.fields["U_MGR_SG"]?.value.toDoubleOrDefault(0.0).toString()
        reportFields["R_SPEC_MGR_SG"] = PreFillModel.testTypeProp.value.fields["R_MGR_SG"]?.value.toDoubleOrDefault(0.0).toString()


        reportFields["U_SPEC_HV_MPT"]       = PreFillModel.testTypeProp.value.fields["U_HV_MPT"]?.value.toDoubleOrDefault(0.0).toString()
        reportFields["I_SPEC_HV_MPT"]       = PreFillModel.testTypeProp.value.fields["I_HV_MPT"]?.value.toDoubleOrDefault(0.0).toString()
        reportFields["TIME_SPEC_HV_MPT"]    = PreFillModel.testTypeProp.value.fields["T_HV_MPT"]?.value.toDoubleOrDefault(0.0).toString()

        reportFields["U_SPEC_HV_SG"]    = PreFillModel.testTypeProp.value.fields["U_HV_SG"]?.value.toDoubleOrDefault(0.0).toString()
        reportFields["I_SPEC_HV_SG"]    = PreFillModel.testTypeProp.value.fields["I_HV_SG"]?.value.toDoubleOrDefault(0.0).toString()
        reportFields["TIME_SPEC_HV_SG"] = PreFillModel.testTypeProp.value.fields["T_HV_SG"]?.value.toDoubleOrDefault(0.0).toString()

        reportFields["R_SPEC_IKAS"] = PreFillModel.testTypeProp.value.fields["R_IKAS_SG"]?.value.toDoubleOrDefault(0.0).toString()

        reportFields["TIME_MEAS_IDLE"] = PreFillModel.testTypeProp.value.fields["IDLE_TIME"]?.value.toDoubleOrDefault(0.0).toString()
        reportFields["TIME_MEAS_LOAD"] = PreFillModel.testTypeProp.value.fields["LOAD_TIME"]?.value.toDoubleOrDefault(0.0).toString()
        //TODO может время реальное

        reportFields["CUSTOMER"] = Properties.standData.customer
        reportFields["CUSTOMER_PLACE"] = Properties.standData.customerPlace
        reportFields["STAND_AIEL"] = Properties.standData.aiel
        reportFields["STAND_SERIAL_NUMBER"] = Properties.standData.serialNumber
        reportFields["MANUFACTURE"] = Properties.standData.manufacture
        reportFields["OPERATOR_POS_1"] = Properties.standData.login1Title
        reportFields["OPERATOR_POS_2"] = Properties.standData.login2Title

        saveProtocol(this)
    }

    fun stopReload() {
        if (isRunning) {
            stop()
        } else if (isFinished) {
            start()
        }
    }

    fun stop() {
        cause = "отменено оператором"
    }

    override fun toString() = "$order. $name"
}
