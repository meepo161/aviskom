package ru.avem.stand.modules.r.tests.pi.startmi

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.i.views.TestViewModule
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

class StartMIView(title: String = "ПУСК. (ТОК / МОМЕНТ)", showOnStart: Boolean = true) :
    TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: StartMI

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = StartMI()
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
        hbox {
            alignment = Pos.CENTER
            tableview(observableList(test.testModel.specifiedData, test.testModel.measuredData)) {
                minHeight = 97.0
                maxHeight = 97.0
                minWidth = 500.0
                columnResizePolicy = SmartResize.POLICY

                hboxConstraints {
                    hGrow = Priority.NEVER
                }

                mouseTransparentProperty().set(true)
                column("", StartMIData::descriptor.getter).also {
                    it.minWidth = 80.0
                }
                column("Uср., В", StartMIData::U.getter)
                column("U AB, В", StartMIData::UAB.getter)
                column("U BC, В", StartMIData::UBC.getter)
                column("U CA, В", StartMIData::UCA.getter)
                column("Iср., А", StartMIData::I.getter)
                column("I A, А", StartMIData::IA.getter)
                column("I B, А", StartMIData::IB.getter)
                column("I C, А", StartMIData::IC.getter)
                column("M, Н⋅м", StartMIData::torque.getter)
                column("Результат", StartMIData::result.getter).also {
                    it.minWidth = 80.0
                }
            }
        }
    }.addClass(Styles.paneBorders)
}
