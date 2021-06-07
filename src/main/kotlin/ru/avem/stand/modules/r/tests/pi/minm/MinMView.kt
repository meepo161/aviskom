package ru.avem.stand.modules.r.tests.pi.minm

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.i.views.TestViewModule
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

class MinMView(title: String = "МИНИМУМ (МОМЕНТ)", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: MinM

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = MinM()
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
                column("U AB, В", MinMData::UAB.getter)
                column("U BC, В", MinMData::UBC.getter)
                column("U CA, В", MinMData::UCA.getter)
                column("I A, А", MinMData::IA.getter)
                column("I B, А", MinMData::IB.getter)
                column("I C, А", MinMData::IC.getter)
            }
            tableview(observableList(test.testModel.specifiedData)) {
                minHeight = 64.0
                maxHeight = 64.0
                minWidth = 500.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                column("U, В", MinMData::U.getter)
                column("I, А", MinMData::I.getter)
            }
        }
        tableview(observableList(test.testModel.measuredData)) {
            minHeight = 64.0
            maxHeight = 64.0
            minWidth = 500.0
            maxWidth = 785.0
            columnResizePolicy = SmartResize.POLICY
            mouseTransparentProperty().set(true)
//            alignment = Pos.CENTER_LEFT TODO
            column("Uср., В", MinMData::U.getter)
            column("Iср., А", MinMData::I.getter)
            column("Результат", MinMData::result.getter).also {
                it.minWidth = 80.0
            }
        }
        tableview(observableList(test.testModel.measuredData)) {
            minHeight = 64.0
            maxHeight = 64.0
            minWidth = 500.0
            columnResizePolicy = SmartResize.POLICY
            mouseTransparentProperty().set(true)
            column("M, Н⋅м", MinMData::torque.getter)
            column("rpm, об/мин", MinMData::RPM.getter)
        }
    }.addClass(Styles.paneBorders)
}
