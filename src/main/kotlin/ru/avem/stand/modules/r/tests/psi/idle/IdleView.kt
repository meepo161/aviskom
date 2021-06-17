package ru.avem.stand.modules.r.tests.psi.idle

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.i.views.TestViewModule
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

class IdleView(title: String = "ХХ", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: Idle

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = Idle()
        }
    }

    override fun EventTarget.testData() = vbox(spacing = 16) {
        padding = insets(8)

        hboxConstraints {
            hGrow = Priority.ALWAYS
        }

        label("Измеренные значения") {
            alignment = Pos.TOP_CENTER
            textAlignment = TextAlignment.CENTER
            useMaxWidth = true
            isWrapText = true
        }
        separator()
        hbox {
            hbox {
                tableview(observableList(test.testModel.measuredData)) {
                    minHeight = 60.0
                    maxHeight = 60.0
                    minWidth = 150.0 * 4
                    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                    mouseTransparentProperty().set(true)

                    alignment = Pos.CENTER

                    column("PV23, В", IdleData::UA.getter)
                    column("PV25, В", IdleData::UB.getter)
                    column("PV24, А", IdleData::IA.getter)
                    column("PV27, А", IdleData::IB.getter)
                }
            }
                tableview(observableList(test.testModel.measuredData)) {
                    minHeight = 60.0
                    maxHeight = 60.0
                    minWidth = 150.0 * 2
                    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                    mouseTransparentProperty().set(true)

                    alignment = Pos.CENTER

                    column("t BK1, °C", IdleData::tempAmb.getter)
                    column("t BK2, °C", IdleData::tempTI.getter)
                }
            }
        hbox {
            vboxConstraints {
                vGrow = Priority.ALWAYS
            }
            tableview(observableList(test.testModel.measuredData)) {
                minHeight = 60.0
                maxHeight = 60.0
                minWidth = 200.0 * 2
                prefWidth = 200.0 * 2
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                mouseTransparentProperty().set(true)

                alignment = Pos.CENTER

                column("Время", IdleData::time.getter)
                column("Результат", IdleData::result.getter)
            }
        }
    }.addClass(Styles.paneBorders, Styles.measuringTable)
}
