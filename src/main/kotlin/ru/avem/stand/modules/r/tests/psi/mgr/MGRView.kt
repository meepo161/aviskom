package ru.avem.stand.modules.r.tests.psi.mgr

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

class MGRView(title: String = "МГР", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: MGR

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = MGR()
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

                    column("U, В", MGRData::U.getter)
                    column("R(за 15 с.),МОм", MGRData::R15.getter)
                    column("R(за 60 с.),МОм", MGRData::R60.getter)
                    column("kABS,о.е.", MGRData::K_ABS.getter)
                }
                tableview(observableList(test.testModel.measuredData)) {
                    minHeight = 60.0
                    maxHeight = 60.0
                    minWidth = 200.0 * 2
                    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                    mouseTransparentProperty().set(true)

                    alignment = Pos.CENTER

                    column("t воздуха,°C", MGRData::tempAmb.getter)
                    column("t ОИ,°C", MGRData::tempTI.getter)
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
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                mouseTransparentProperty().set(true)


                column("Результат", MGRData::result.getter)
            }
        }
    }.addClass(Styles.paneBorders, Styles.measuringTable)
}
