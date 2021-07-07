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

        label("Измеренные значения МПТ") {
            alignment = Pos.CENTER
            textAlignment = TextAlignment.CENTER
            useMaxWidth = true
            isWrapText = true
        }
        tableview(observableList(test.testModel.measuredData)) {
            minHeight = 60.0
            maxHeight = 60.0
            minWidth = 150.0 * 6
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            mouseTransparentProperty().set(true)
//            column("Uср., В", LoadData::U.getter)
//            column("Iср., А", LoadData::I.getter)
            column("Uя, В", LoadData::U_Y_MPT.getter)   //PV23
            column("Iя, А", LoadData::I_Y_MPT.getter)  //PV24
            column("Uов, В", LoadData::U_V_MPT.getter)   //PV25
            column("Iов, А", LoadData::I_V_MPT.getter)  //PV27
            column("t возд., °C", LoadData::tempAmb.getter)
            column("t ОИ, °C", LoadData::tempTI.getter)
//            column("Результат", LoadData::result.getter)
        }
        label("Измеренные значения СГ") {
            alignment = Pos.CENTER
            textAlignment = TextAlignment.CENTER
            useMaxWidth = true
            isWrapText = true
        }
        tableview(observableList(test.testModel.measuredData)) {
            minHeight = 60.0
            maxHeight = 60.0
            minWidth = 150.0 * 6
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            mouseTransparentProperty().set(true)
            column("U AB, В", LoadData::UAB.getter)
            column("U BC, В", LoadData::UBC.getter)
            column("U CA, В", LoadData::UCA.getter)
            column("I A, А", LoadData::IA.getter)
            column("I B, А", LoadData::IB.getter)
            column("I C, А", LoadData::IC.getter)
//            column("cos φ", LoadData::cos.getter)
//            column("P1, кВт", LoadData::P1.getter)
//            column("P, кВт", LoadData::P2.getter)
        }
        tableview(observableList(test.testModel.measuredData)) {
            minHeight = 60.0
            maxHeight = 60.0
            minWidth = 150.0 * 5
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            mouseTransparentProperty().set(true)
            column("Uов, В", LoadData::U_V_SG.getter)
            column("Iов, А", LoadData::I_V_SG.getter)
            column("n, об/мин", LoadData::F.getter)
            column("Результат", LoadData::result.getter)
        }
//        tableview(observableList(test.testModel.measuredData)) {
//            minHeight = 60.0
//            maxHeight = 60.0
//            minWidth = 150.0 * 6
//            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
//            mouseTransparentProperty().set(true)
//            column("M, Н⋅м", LoadData::torque.getter)
//            column("rpm, об/мин", LoadData::RPM.getter)
//            column("КПД, о.е.", LoadData::efficiency.getter)
//            column("Скольжение, %", LoadData::sk.getter)
//        }
//        hbox {
//            hboxConstraints {
//                vgrow = Priority.ALWAYS
//            }
//            alignment = Pos.BOTTOM_CENTER
//            tableview(observableList(test.testModel.measuredData)) {
//                minHeight = 60.0
//                maxHeight = 60.0
//                minWidth = 200.0
//                prefWidth = 200.0
//                columnResizePolicy = SmartResize.POLICY
//                mouseTransparentProperty().set(true)
//
//                column("Результат", LoadData::result.getter)
//            }
//        }
    }.addClass(Styles.paneBorders, Styles.measuringTable)
}
