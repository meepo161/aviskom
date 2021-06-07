package ru.avem.stand.modules.r.tests.pi.maxm

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

class MaxMView(title: String = "МАКСИМУМ (МОМЕНТ)", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: MaxM

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = MaxM()
        }
    }

    override fun EventTarget.testData() = vbox(spacing = 16) {
        padding = insets(8)

        hboxConstraints {
            hGrow = Priority.ALWAYS
        }

        label("Измеренные значения") {
            alignment = Pos.CENTER
            textAlignment = TextAlignment.CENTER
            useMaxWidth = true
            isWrapText = true
        }
        hbox(spacing = 16) {
            tableview(observableList(test.testModel.measuredData)) {
                minHeight = 64.0
                maxHeight = 64.0
                minWidth = 500.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                column("U AB, В", MaxMData::UAB.getter)
                column("U BC, В", MaxMData::UBC.getter)
                column("U CA, В", MaxMData::UCA.getter)
                column("I A, А", MaxMData::IA.getter)
                column("I B, А", MaxMData::IB.getter)
                column("I C, А", MaxMData::IC.getter)
                column("cos φ", MaxMData::cos.getter)
                column("P1, кВт", MaxMData::P1.getter)
                column("P2, кВт", MaxMData::P2.getter)
            }
            tableview(observableList(test.testModel.specifiedData)) {
                minHeight = 64.0
                maxHeight = 64.0
                minWidth = 500.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                column("U, В", MaxMData::U.getter)
                column("I, А", MaxMData::I.getter)
                column("cos φ", MaxMData::cos.getter)
                column("P2, кВт", MaxMData::P2.getter)
            }
        }
        tableview(observableList(test.testModel.measuredData)) {
            minHeight = 64.0
            maxHeight = 64.0
            minWidth = 500.0
            maxWidth = 785.0
            columnResizePolicy = SmartResize.POLICY
            mouseTransparentProperty().set(true)
            alignment = Pos.CENTER_LEFT // TODO
            column("Uср., В", MaxMData::U.getter)
            column("Iср., А", MaxMData::I.getter)
            column("Результат", MaxMData::result.getter).also {
                it.minWidth = 80.0
            }
        }
        tableview(observableList(test.testModel.measuredData)) {
            minHeight = 64.0
            maxHeight = 64.0
            minWidth = 500.0
            columnResizePolicy = SmartResize.POLICY
            mouseTransparentProperty().set(true)
            column("M, Н⋅м", MaxMData::torque.getter)
        }
    }.addClass(Styles.paneBorders)
}
