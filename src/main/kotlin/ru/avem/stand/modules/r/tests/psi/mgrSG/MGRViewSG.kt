package ru.avem.stand.modules.r.tests.psi.mgrSG

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.i.views.TestViewModule
import ru.avem.stand.modules.r.tests.psi.mgr.MGRData
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

class MGRViewSG(title: String = "МГР", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: MGRSG

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = MGRSG()
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
            alignment = Pos.CENTER
            vbox(32.0) {
                alignment = Pos.CENTER
                tableview(observableList(test.testModel.measuredData)) {
                    minHeight = 60.0
                    maxHeight = 60.0
                    minWidth = 200.0 * 4
                    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                    mouseTransparentProperty().set(true)

                    alignment = Pos.CENTER

                    column("U, В", MGRDataSG::U.getter)
                    column("R(за 15 с.),МОм", MGRDataSG::R15.getter)
                    column("R(за 60 с.),МОм", MGRDataSG::R60.getter)
                    column("kABS,о.е.", MGRDataSG::K_ABS.getter)
                }
                tableview(observableList(test.testModel.measuredData)) {
                    minHeight = 60.0
                    maxHeight = 60.0
                    minWidth = 200.0 * 2
                    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                    mouseTransparentProperty().set(true)

                    alignment = Pos.CENTER

                    column("t воздуха,°C", MGRDataSG::tempAmb.getter)
                    column("t ОИ,°C", MGRDataSG::tempTI.getter)
                }
            }
        }
        hbox {
            hboxConstraints {
                vgrow = Priority.ALWAYS
            }
            alignment = Pos.BOTTOM_CENTER
            tableview(observableList(test.testModel.measuredData)) {
                minHeight = 60.0
                maxHeight = 60.0
                minWidth = 200.0
                prefWidth = 200.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)

                column("Результат", MGRDataSG::result.getter)
            }
        }
    }.addClass(Styles.paneBorders, Styles.measuringTable)
}
