package ru.avem.stand.modules.r.tests.pi.load

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

class LoadView(title: String = "НАГР", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: Load

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = Load()
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
        separator()
        vbox(spacing = 16) {
            tableview(observableList(test.testModel.measuredData)) {
                minHeight = 60.0
                maxHeight = 60.0
                minWidth = 150.0 * 9
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                mouseTransparentProperty().set(true)
                column("U AB, В", LoadData::UAB.getter)
                column("U BC, В", LoadData::UBC.getter)
                column("U CA, В", LoadData::UCA.getter)
                column("I A, А", LoadData::IA.getter)
                column("I B, А", LoadData::IB.getter)
                column("I C, А", LoadData::IC.getter)
                column("cos φ", LoadData::cos.getter)
                column("P1, кВт", LoadData::P1.getter)
                column("P2, кВт", LoadData::P2.getter)
            }
            tableview(observableList(test.testModel.specifiedData)) {
                minHeight = 60.0
                maxHeight = 60.0
                minWidth = 150.0 * 4
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                mouseTransparentProperty().set(true)
                column("U, В", LoadData::U.getter)
                column("I, А", LoadData::I.getter)
                column("cos φ", LoadData::cos.getter)
                column("P2, кВт", LoadData::P2.getter)
            }
        }
        tableview(observableList(test.testModel.measuredData)) {
            minHeight = 60.0
            maxHeight = 60.0
            minWidth = 150.0 * 7
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            mouseTransparentProperty().set(true)
            column("Uср., В", LoadData::U.getter)
            column("Iср., А", LoadData::I.getter)
            column("PV23, В", LoadData::UA.getter)
            column("PV25, В", LoadData::UB.getter)
            column("PV24, А", LoadData::I2A.getter)
            column("PV27, А", LoadData::I2B.getter)
            column("Результат", LoadData::result.getter)
        }
        tableview(observableList(test.testModel.measuredData)) {
            minHeight = 60.0
            maxHeight = 60.0
            minWidth = 150.0 * 6
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            mouseTransparentProperty().set(true)
            column("tAmb, °C", LoadData::tempAmb.getter)
            column("tempTI, °C", LoadData::tempTI.getter)
            column("M, Н⋅м", LoadData::torque.getter)
            column("rpm, об/мин", LoadData::RPM.getter)
            column("КПД, о.е.", LoadData::efficiency.getter)
            column("Скольжение, %", LoadData::sk.getter)
        }
    }.addClass(Styles.paneBorders, Styles.measuringTable)
}
