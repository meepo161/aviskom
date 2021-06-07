package ru.avem.stand.modules.r.tests.pi.overm

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.i.views.TestViewModule
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

class OverMView(title: String = "ПЕРЕГРУЗКА (МОМЕНТ)", showOnStart: Boolean = true) :
    TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: OverM

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = OverM()
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
                column("U AB, В", OverMData::UAB.getter)
                column("U BC, В", OverMData::UBC.getter)
                column("U CA, В", OverMData::UCA.getter)
                column("I A, А", OverMData::IA.getter)
                column("I B, А", OverMData::IB.getter)
                column("I C, А", OverMData::IC.getter)
                column("cos φ", OverMData::cos.getter)
                column("P1, кВт", OverMData::P1.getter)
            }
            tableview(observableList(test.testModel.specifiedData)) {
                minHeight = 64.0
                maxHeight = 64.0
                minWidth = 500.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                column("U, В", OverMData::U.getter)
                column("I, А", OverMData::I.getter)
                column("cos φ", OverMData::cos.getter)
            }
        }
        hbox(spacing = 16) {
            tableview(observableList(test.testModel.measuredData)) {
                minHeight = 64.0
                maxHeight = 64.0
                minWidth = 642.0
                maxWidth = 642.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                alignment = Pos.CENTER_LEFT
                column("Uср., В", OverMData::U.getter)
                column("Iср., А", OverMData::I.getter)
            }
            tableview(observableList(test.testModel.measuredData)) {
                minHeight = 64.0
                maxHeight = 64.0
                minWidth = 500.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                column("M, Н⋅м", OverMData::torque.getter)
                column("rpm, об/мин", OverMData::RPM.getter)
                column("t, °C", OverMData::T.getter)
                column("Результат", OverMData::result.getter).also {
                    it.minWidth = 80.0
                }
            }
        }
    }.addClass(Styles.paneBorders)
}
