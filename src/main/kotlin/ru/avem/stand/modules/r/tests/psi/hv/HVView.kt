package ru.avem.stand.modules.r.tests.psi.hv

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.i.views.TestViewModule
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

class HVView(title: String = "ВИУ", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: HV

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = HV()
        }
    }

    override fun EventTarget.testData() = vbox(spacing = 16) {
        padding = insets(8)

        hboxConstraints {
            hGrow = Priority.ALWAYS
        }

        label("Заданные значения") {
            alignment = Pos.TOP_CENTER
            textAlignment = TextAlignment.CENTER
            useMaxWidth = true
            isWrapText = true
        }
        hbox {
            tableview(observableList(test.testModel.initData)) {
                minHeight = 60.0
                maxHeight = 60.0
                minWidth = 150.0 * 2
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                mouseTransparentProperty().set(true)

                alignment = Pos.CENTER
                HVModel.initData.U.set(PreFillModel.testTypeProp.value.fields["U_HV_MPT"]?.value.toDoubleOrDefault(0.0).toString())
                HVModel.initData.I.set(PreFillModel.testTypeProp.value.fields["I_HV_MPT"]?.value.toDoubleOrDefault(0.0).toString())
                column("U, В", HVData::U.getter)
                column("I, А", HVData::I.getter)
            }
        }
        separator()
        label("Измеренные значения") {
            alignment = Pos.TOP_CENTER
            textAlignment = TextAlignment.CENTER
            useMaxWidth = true
            isWrapText = true
        }
        hbox {
            tableview(observableList(test.testModel.measuredData)) {
                minHeight = 60.0
                maxHeight = 60.0
                minWidth = 150.0 * 3
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                mouseTransparentProperty().set(true)

                alignment = Pos.CENTER

                column("U, В", HVData::U.getter)
                column("I, А", HVData::I.getter)
            }
        }
        hbox {
            vboxConstraints {
                vGrow = Priority.ALWAYS
            }
            alignment = Pos.BOTTOM_CENTER
            tableview(observableList(test.testModel.measuredData)) {
                minHeight = 60.0
                maxHeight = 60.0
                minWidth = 200.0 *2
                prefWidth = 200.0*2
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                mouseTransparentProperty().set(true)


                column("Время, с", HVData::time.getter)
                column("Результат", HVData::result.getter)
            }
        }
    }.addClass(Styles.paneBorders, Styles.measuringTable)
}
