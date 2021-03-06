package ru.avem.stand.modules.i.views

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.scene.control.TableRow
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import ru.avem.stand.modules.i.tests.Test
import ru.avem.stand.modules.r.common.prefill.isCancelAllTests
import ru.avem.stand.modules.r.common.prefill.isTestRunning
import ru.avem.stand.modules.r.tests.Protection
import ru.avem.stand.modules.r.tests.TestItemData
import tornadofx.*


abstract class TestViewModule(title: String, showOnStart: Boolean = false) : ViewModule(title, showOnStart) {
    abstract val test: Test

    var cancelAllTestsButton: Button by singleAssign()
    var stopReloadButton: Button by singleAssign()
    var nextTestButton: Button by singleAssign()

    var vBoxLog: VBox by singleAssign()

    override fun onBeforeShow() {
        super.onBeforeShow()
        currentWindow?.setOnCloseRequest {
            showCancelConfirmation(it, currentWindow, currentStage, test)
        }
    }

    override fun onDock() {
        super.onDock()
        test.start()
    }

    override val root = vbox(spacing = 16) {
        injectTest()
        padding = insets(16)

//        background = TFXViewManager.mainBackground

        label(test.name) {
            alignment = Pos.CENTER
            textAlignment = TextAlignment.CENTER
            useMaxWidth = true
            isWrapText = true
        }
        hbox(spacing = 16) {
            padding = insets(16)

                specifiedData()

                testData()

                protections()
        }.addClass(Styles.paneBoldBorders)

        anchorpane {
            scrollpane {
                anchorpaneConstraints {
                    leftAnchor = 0.0
                    rightAnchor = 0.0
                    topAnchor = 0.0
                    bottomAnchor = 0.0
                }
                minHeight = 250.0
                maxHeight = 250.0
                prefHeight = 250.0
                minWidth = 900.0
                prefWidth = 900.0
                vBoxLog = vbox {
                }.addClass(Styles.megaHard)

                vvalueProperty().bind(vBoxLog.heightProperty())
            }
        }
        hbox {
            progressbar(property = test.testModel.progressProperty) {
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                }
                useMaxWidth = true
            }
        }
        hbox(spacing = 16) {
            alignment = Pos.CENTER

            cancelAllTestsButton = button("???????????????? ??????") {
                minWidth = 150.0
                graphic = MaterialDesignIconView(MaterialDesignIcon.ARROW_LEFT_BOLD).apply {
                    glyphSize = 45
                    fill = c("red")
                }
                contentDisplay = ContentDisplay.TOP

                action {
                    currentStage?.close()
                    isCancelAllTests = true
                    isTestRunning = false
                }
            }

            stopReloadButton = button("????????") {
                isDefaultButton = true
                minWidth = 150.0
                graphic = MaterialDesignIconView(MaterialDesignIcon.STOP).apply {
                    glyphSize = 45
                    fill = c("red")
                }
                contentDisplay = ContentDisplay.TOP

                action {
                    test.stopReload()
                }
            }

            nextTestButton = button("?? ????????????????????") {
                minWidth = 150.0
                graphic = MaterialDesignIconView(MaterialDesignIcon.ARROW_RIGHT_BOLD).apply {
                    glyphSize = 45
                    fill = c("black")
                }
                contentDisplay = ContentDisplay.TOP

                action {
                    currentStage?.close()
                    isTestRunning = false
                }
            }
        }

    }.addClass(Styles.extraHard)

    abstract fun injectTest()

    abstract fun EventTarget.testData(): VBox

    fun EventTarget.specifiedData(): VBox {
        return vbox(2) {
            maxWidth = 320.0
            padding = insets(8)

            hboxConstraints {
                hGrow = Priority.NEVER
            }

            alignmentProperty().set(Pos.TOP_CENTER)

            label("????????????????") {
                alignment = Pos.CENTER
                minWidth = 20.0
            }
            hbox(16) {
                vbox(16) {
                    maxWidth = 120.0
//                    tableview(observableList(test.testModel.testItemData)) {
//                        minHeight = 88.0
//                        maxHeight = 88.0
//                        columnResizePolicy = SmartResize.POLICY
//                        mouseTransparentProperty().set(true)
//                        column("P1, ??????", TestItemData::P.getter)
//                    }
                    tableview(observableList(test.testModel.testItemData)) {
                        minHeight = 88.0
                        maxHeight = 88.0
                        columnResizePolicy = SmartResize.POLICY
                        mouseTransparentProperty().set(true)
                        column("U??, ??", TestItemData::U_Y_MPT.getter)
                    }
                    tableview(observableList(test.testModel.testItemData)) {
                        minHeight = 88.0
                        maxHeight = 88.0
                        columnResizePolicy = SmartResize.POLICY
                        mouseTransparentProperty().set(true)
                        column("I??, ??", TestItemData::I_Y_MPT.getter)
                    }
                    tableview(observableList(test.testModel.testItemData)) {
                        minHeight = 88.0
                        maxHeight = 88.0
                        columnResizePolicy = SmartResize.POLICY
                        mouseTransparentProperty().set(true)
                        column("U??, ??", TestItemData::U_V_MPT.getter)
                    }
                    tableview(observableList(test.testModel.testItemData)) {
                        minHeight = 88.0
                        maxHeight = 88.0
                        columnResizePolicy = SmartResize.POLICY
                        mouseTransparentProperty().set(true)
                        column("I??, ??", TestItemData::I_V_MPT.getter)
                    }
//                    tableview(observableList(test.testModel.testItemData)) {
//
//                        minHeight = 88.0
//                        maxHeight = 88.0
//                        columnResizePolicy = SmartResize.POLICY
//                        mouseTransparentProperty().set(true)
//                        column("cos ??", TestItemData::cos.getter)
//                    }
                }
//                vbox(16) {
//                    tableview(observableList(test.testModel.testItemData)) {
//
//                        minHeight = 88.0
//                        maxHeight = 88.0
//                        columnResizePolicy = SmartResize.POLICY
//                        mouseTransparentProperty().set(true)
//                        column("n, ????/??????", TestItemData::RPM.getter)
//                    }
//                    tableview(observableList(test.testModel.testItemData)) {
//
//                        minHeight = 88.0
//                        maxHeight = 88.0
//                        columnResizePolicy = SmartResize.POLICY
//                        mouseTransparentProperty().set(true)
//                        column("f, ????", TestItemData::F.getter)
//                    }
//                    tableview(observableList(test.testModel.testItemData)) {
//
//                        minHeight = 88.0
//                        maxHeight = 88.0
//                        columnResizePolicy = SmartResize.POLICY
//                        mouseTransparentProperty().set(true)
//                        column("??????, %", TestItemData::efficiency.getter)
//                    }
//                }
            }
        }.addClass(Styles.paneBorders)
    }

    private fun EventTarget.protections(): VBox {
        return vbox(2) {
            maxWidth = 420.0
            padding = insets(8)

            hboxConstraints {
                hGrow = Priority.NEVER
            }

            alignmentProperty().set(Pos.TOP_CENTER)

            label("?????????????????? ??????????") {
                alignment = Pos.CENTER
                minWidth = 80.0
            }
            hbox(spacing = 16.0) {
                vbox(16) {
                    tableview(observableList(test.testModel.protections.overcurrentTI)) {
                        minHeight = 88.0
                        maxHeight = 88.0
                        columnResizePolicy = SmartResize.POLICY
                        mouseTransparentProperty().set(true)
                        column("?????????????? ????", Protection::prop.getter)

                        setRowFactory {
                            object : TableRow<Protection>() {
                                override fun updateItem(item: Protection?, empty: Boolean) {
                                    super.updateItem(item, empty)
                                    item?.let {
                                        style {
                                            baseColor = item.color()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    tableview(observableList(test.testModel.protections.overcurrentHV)) {

                        minHeight = 88.0
                        maxHeight = 88.0
                        columnResizePolicy = SmartResize.POLICY
                        mouseTransparentProperty().set(true)
                        column("?????????????? ??????", Protection::prop.getter)

                        setRowFactory {
                            object : TableRow<Protection>() {
                                override fun updateItem(item: Protection?, empty: Boolean) {
                                    super.updateItem(item, empty)
                                    item?.let {
                                        style {
                                            baseColor = item.color()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    tableview(observableList(test.testModel.protections.doorsPEC)) {

                        minHeight = 88.0
                        maxHeight = 88.0
                        columnResizePolicy = SmartResize.POLICY
                        mouseTransparentProperty().set(true)
                        column("?????????? ??????", Protection::prop.getter)

                        setRowFactory {
                            object : TableRow<Protection>() {
                                override fun updateItem(item: Protection?, empty: Boolean) {
                                    super.updateItem(item, empty)
                                    item?.let {
                                        style {
                                            baseColor = item.color()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    tableview(observableList(test.testModel.protections.PE)) {

                        minHeight = 88.0
                        maxHeight = 88.0
                        columnResizePolicy = SmartResize.POLICY
                        mouseTransparentProperty().set(true)
                        column("?????????? ????????", Protection::prop.getter)

                        setRowFactory {
                            object : TableRow<Protection>() {
                                override fun updateItem(item: Protection?, empty: Boolean) {
                                    super.updateItem(item, empty)
                                    item?.let {
                                        style {
                                            baseColor = item.color()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                vbox(16) {
                    tableview(observableList(test.testModel.protections.contactPosts)) {

                        minHeight = 88.0
                        maxHeight = 88.0
                        columnResizePolicy = SmartResize.POLICY
                        mouseTransparentProperty().set(true)
                        column("??????????????. ????????", Protection::prop.getter)

                        setRowFactory {
                            object : TableRow<Protection>() {
                                override fun updateItem(item: Protection?, empty: Boolean) {
                                    super.updateItem(item, empty)
                                    item?.let {
                                        style {
                                            baseColor = item.color()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    tableview(observableList(test.testModel.protections.earthingSwitch)) {

                        minHeight = 88.0
                        maxHeight = 88.0
                        columnResizePolicy = SmartResize.POLICY
                        mouseTransparentProperty().set(true)
                        column("??????????????????????", Protection::prop.getter)

                        setRowFactory {
                            object : TableRow<Protection>() {
                                override fun updateItem(item: Protection?, empty: Boolean) {
                                    super.updateItem(item, empty)
                                    item?.let {
                                        style {
                                            baseColor = item.color()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    tableview(observableList(test.testModel.protections.overheatingLM1)) {

                        minHeight = 88.0
                        maxHeight = 88.0
                        columnResizePolicy = SmartResize.POLICY
                        mouseTransparentProperty().set(true)
                        column("???????????????? ????-1", Protection::prop.getter)

                        setRowFactory {
                            object : TableRow<Protection>() {
                                override fun updateItem(item: Protection?, empty: Boolean) {
                                    super.updateItem(item, empty)
                                    item?.let {
                                        style {
                                            baseColor = item.color()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    tableview(observableList(test.testModel.protections.overheatingLM2)) {

                        minHeight = 88.0
                        maxHeight = 88.0
                        columnResizePolicy = SmartResize.POLICY
                        mouseTransparentProperty().set(true)
                        column("???????????????? ????-2", Protection::prop.getter)

                        setRowFactory {
                            object : TableRow<Protection>() {
                                override fun updateItem(item: Protection?, empty: Boolean) {
                                    super.updateItem(item, empty)
                                    item?.let {
                                        style {
                                            baseColor = item.color()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

//            children.filter { it is TableView<*> }.forEach {
//                (it as TableView<Protection>).setRowFactory {
//                    object : TableRow<Protection>() {
//                        override fun updateItem(item: Protection?, empty: Boolean) {
//                            super.updateItem(item, empty)
//                            item?.let {
//                                style {
//                                    baseColor = item.color()
//                                }
//                            }
//                        }
//                    }
//                }
//            }
        }.addClass(Styles.paneBorders)
    }
}
