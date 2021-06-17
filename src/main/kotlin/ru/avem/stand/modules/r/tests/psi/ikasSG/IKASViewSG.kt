package ru.avem.stand.modules.r.tests.psi.ikasSG

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

class IKASViewSG(title: String = "ИКАС", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: IKASSG

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = IKASSG()
        }
    }

    override fun EventTarget.testData() = vbox(spacing = 16) {
        padding = insets(8)

        hboxConstraints {
            hGrow = Priority.ALWAYS
        }

        separator()
        hbox {
            alignment = Pos.CENTER
                vbox(6.0) {
                    label("Измеренные значения") {
                        alignment = Pos.TOP_CENTER
                        textAlignment = TextAlignment.CENTER
                        useMaxWidth = true
                        isWrapText = true
                    }
                    hbox {
                        tableview(
                            observableList(test.testModel.measuredData)
                        ) {
                            minHeight = 60.0
                            maxHeight = 60.0
                            minWidth = 150.0 * 4
                            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                            mouseTransparentProperty().set(true)

                            alignment = Pos.CENTER

                            column("R12 | R1, Ом", IKASDataSG::R1.getter)
                            column("R23 | R2, Ом", IKASDataSG::R2.getter)
                            column("R31 | R3, Ом", IKASDataSG::R3.getter)
                        }
                        tableview(observableList(test.testModel.measuredData)) {
                            minHeight = 60.0
                            maxHeight = 60.0
                            minWidth = 150.0 * 2
                            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                            mouseTransparentProperty().set(true)

                            alignment = Pos.CENTER

                            column("t BK1, °C", IKASDataSG::tempAmb.getter)
                            column("t BK2, °C", IKASDataSG::tempTI.getter)
                        }
                    }
                    separator()
                    label("Приведенные к 20 градусам цельсия") {
                        alignment = Pos.TOP_CENTER
                        textAlignment = TextAlignment.CENTER
                        useMaxWidth = true
                        isWrapText = true
                    }
                    tableview(
                        observableList(test.testModel.calculatedR20Data)
                    ) {
                        minHeight = 60.0
                        maxHeight = 60.0
                        minWidth = 150.0 * 4
                        columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                        mouseTransparentProperty().set(true)

                        alignment = Pos.CENTER

                        column("R12 | R1, Ом", IKASDataSG::R1.getter)
                        column("R23 | R2, Ом", IKASDataSG::R2.getter)
                        column("R31 | R3, Ом", IKASDataSG::R3.getter)
                    }
            }
        }
        hbox {
            alignment = Pos.CENTER
            vboxConstraints {
                vGrow = Priority.ALWAYS
            }
            tableview(observableList(test.testModel.measuredData)) {
                minHeight = 60.0
                maxHeight = 60.0
                minWidth = 200.0
                prefWidth = 200.0
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                mouseTransparentProperty().set(true)


                column("Результат", IKASDataSG::result.getter)
            }
        }
    }.addClass(Styles.paneBorders, Styles.measuringTable)
}
