package ru.avem.stand.modules.r.tests.psi.kz

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.i.views.TestViewModule
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

class KZView(title: String = "КЗ", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: KZ

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = KZ()
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
            column("", KZData::descriptor.getter).also {
                it.minWidth = 80.0
            }
            column("Uср., В", KZData::U.getter)
            column("U AB, В", KZData::UAB.getter)
            column("U BC, В", KZData::UBC.getter)
            column("U CA, В", KZData::UCA.getter)
            column("Iср., А", KZData::I.getter)
            column("I A, А", KZData::IA.getter)
            column("I B, А", KZData::IB.getter)
            column("I C, А", KZData::IC.getter)
            column("cos φ", KZData::cos.getter)
            column("P, кВт", KZData::P1.getter)
            column("f, Гц", KZData::F.getter)
            column("Результат", KZData::result.getter).also {
                it.minWidth = 80.0
            }
        }
        tableview(observableList(test.testModel.measuredData)) {
            minHeight = 64.0
            maxHeight = 64.0
            minWidth = 500.0
            columnResizePolicy = SmartResize.POLICY
            mouseTransparentProperty().set(true)
            column("t BK1, °C", KZData::tempAmb.getter)
            column("t BK2, °C", KZData::tempTI.getter)
        }
    }.addClass(Styles.paneBorders)
}
