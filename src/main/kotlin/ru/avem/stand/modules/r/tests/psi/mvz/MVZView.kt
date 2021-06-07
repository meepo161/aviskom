package ru.avem.stand.modules.r.tests.psi.mvz

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

class MVZView(title: String = "МВЗ", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: MVZ

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = MVZ()
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
        tableview(
            observableList(
                test.testModel.measuredDataBefore,
                test.testModel.measuredDataDuring,
                test.testModel.measuredDataAfter
            )
        ) {
            minHeight = 130.0
            maxHeight = 130.0
            minWidth = 500.0
            columnResizePolicy = SmartResize.POLICY
            mouseTransparentProperty().set(true)
            column("", MVZData::descriptor.getter)
            column("Uср., В", MVZData::U.getter)
            column("U AB, В", MVZData::UAB.getter)
            column("U BC, В", MVZData::UBC.getter)
            column("U CA, В", MVZData::UCA.getter)
            column("Iср., А", MVZData::I.getter)
            column("I A, А", MVZData::IA.getter)
            column("I B, А", MVZData::IB.getter)
            column("I C, А", MVZData::IC.getter)
            column("Результат", MVZData::result.getter)
        }
        tableview(observableList(test.testModel.measuredDataDuring)) {
            minHeight = 64.0
            maxHeight = 64.0
            minWidth = 500.0
            columnResizePolicy = SmartResize.POLICY
            mouseTransparentProperty().set(true)
            column("Время, с", MVZData::time.getter)
        }
        tableview(observableList(test.testModel.measuredDataAfter)) {
            minHeight = 64.0
            maxHeight = 64.0
            minWidth = 500.0
            columnResizePolicy = SmartResize.POLICY
            mouseTransparentProperty().set(true)
            column("Отклонение IA, %", MVZData::diffIA.getter)
            column("Отклонение IB, %", MVZData::diffIB.getter)
            column("Отклонение IC, %", MVZData::diffIC.getter)
        }
    }.addClass(Styles.paneBorders)
}
