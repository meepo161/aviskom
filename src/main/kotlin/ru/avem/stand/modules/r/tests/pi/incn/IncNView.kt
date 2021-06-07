package ru.avem.stand.modules.r.tests.pi.incn

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.i.views.TestViewModule
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

class IncNView(title: String = "ПОВЫШЕННАЯ ЧАСТОТА", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: IncN

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = IncN()
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
        tableview(observableList(test.testModel.specifiedData, test.testModel.measuredData)) {
            minHeight = 97.0
            maxHeight = 97.0
            minWidth = 500.0
            columnResizePolicy = SmartResize.POLICY
            mouseTransparentProperty().set(true)
            column("", IncNData::descriptor.getter).also {
                it.minWidth = 80.0
            }
            column("Uср., В", IncNData::U.getter)
            column("U AB, В", IncNData::UAB.getter)
            column("U BC, В", IncNData::UBC.getter)
            column("U CA, В", IncNData::UCA.getter)
            column("Iср., А", IncNData::I.getter)
            column("I A, А", IncNData::IA.getter)
            column("I B, А", IncNData::IB.getter)
            column("I C, А", IncNData::IC.getter)
            column("f, Гц", IncNData::F.getter)
            column("rpm, об/мин", IncNData::RPM.getter)
            column("Время, с", IncNData::time.getter)
            column("Результат", IncNData::result.getter).also {
                it.minWidth = 80.0
            }
        }
    }.addClass(Styles.paneBorders)
}
