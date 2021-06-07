package ru.avem.stand.modules.r.tests.pi.varyuf

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.i.views.TestViewModule
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

class VaryUFView(title: String = "ИЗМ U / F", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: VaryUF

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = VaryUF()
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
            column("", VaryUFData::descriptor.getter).also {
                it.minWidth = 80.0
            }
            column("Uср., В", VaryUFData::U.getter)
            column("U AB, В", VaryUFData::UAB.getter)
            column("U BC, В", VaryUFData::UBC.getter)
            column("U CA, В", VaryUFData::UCA.getter)
            column("Iср., А", VaryUFData::I.getter)
            column("I A, А", VaryUFData::IA.getter)
            column("I B, А", VaryUFData::IB.getter)
            column("I C, А", VaryUFData::IC.getter)
            column("f, Гц", VaryUFData::F.getter)
            column("rpm, об/мин", VaryUFData::RPM.getter)
            column("Результат", VaryUFData::result.getter).also {
                it.minWidth = 80.0
            }
        }
    }.addClass(Styles.paneBorders)
}
