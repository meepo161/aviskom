package ru.avem.stand.modules.r.common.prefill

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ListView
import javafx.scene.input.MouseEvent.MOUSE_PRESSED
import javafx.scene.layout.Priority
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.i.views.showConfirmationTestObject
import ru.avem.stand.modules.r.storage.Properties
import ru.avem.stand.modules.r.storage.database.entities.TestItem
import ru.avem.stand.utils.minusPercent
import tornadofx.*

class PreFillTab : View("Испытания") {
    private val controller: PreFillController by inject()

    private var testTypes: ComboBox<TestItem> by singleAssign()

    var testsList: ListView<CheckBox> by singleAssign()

    override val root = anchorpane {
        vbox(spacing = 16.0) {
            anchorpaneConstraints {
                leftAnchor = 16.0
                rightAnchor = 16.0
                bottomAnchor = 120.0
                topAnchor = 16.0
            }
            alignment = Pos.CENTER

            label("Заполните все поля и нажмите \"Начать испытания\"").addClass(Styles.headerLabels)

            separator()

            hbox(spacing = 16.0) {
                alignment = Pos.CENTER_LEFT
                label("Заводской номер МПТ:") {
                    minWidth = 117.0
                }

                textfield(property = PreFillModel.serialNumberProp) {
                    hboxConstraints {
                        hGrow = Priority.ALWAYS
                    }
                    minWidth = Properties.standData.width minusPercent 50.0

                    text = ""
                }
            }

            hbox(spacing = 16.0) {
                alignment = Pos.CENTER_LEFT
                label("Заводской номер СГ:") {
                    minWidth = 117.0
                }

                textfield(property = PreFillModel.serialNumberPropSG) {
                    hboxConstraints {
                        hGrow = Priority.ALWAYS
                    }
                    minWidth = Properties.standData.width minusPercent 50.0

                    text = ""
                }
            }

            hbox(spacing = 80.0) {
                alignment = Pos.CENTER_LEFT
                label("Тип ОИ:") {
                    minWidth = 52.0
                }

                testTypes = combobox(PreFillModel.testTypeProp, Properties.testItems) {
                    hboxConstraints {
                        hGrow = Priority.ALWAYS
                    }
                    minWidth = Properties.standData.width minusPercent 50.0
                    useMaxWidth = true

                    selectionModel.selectFirst()
                    onAction = EventHandler {
                        PreFillModel.testTypeProp.value = selectedItem
                    }
                }
            }

            separator()

//            tabpane {
//                tabClosingPolicyProperty().set(TabPane.TabClosingPolicy.UNAVAILABLE)
//                tab("ПСИ") {
            label("Выбор испытания:") {
                minWidth = 75.0
            }
            hbox(spacing = 58.0) {
                paddingTop = 8
                vbox(spacing = 16.0) {
                    alignment = Pos.CENTER
                    button("Выбрать всё") {
                        minWidth = 150.0
                        maxWidth = 150.0
                        graphic = MaterialDesignIconView(MaterialDesignIcon.CHECK).apply {
                            glyphSize = 65
                            fill = c("green")
                        }
                        contentDisplay = ContentDisplay.TOP

                        action {
                            text = if (text == "Выбрать всё") {
                                controller.selectAllTests()
                                graphic = MaterialDesignIconView(MaterialDesignIcon.UNDO).apply {
                                    glyphSize = 65
                                    fill = c("red")
                                }
                                "Снять всё"
                            } else {
                                controller.unSelectAllTests()
                                graphic = MaterialDesignIconView(MaterialDesignIcon.CHECK).apply {
                                    glyphSize = 65
                                    fill = c("green")
                                }
                                "Выбрать всё"
                            }
                        }
                    }
                }

                testsList = listview {
                    hboxConstraints {
                        hGrow = Priority.ALWAYS
                    }
                    minHeight = Properties.standData.height minusPercent 52.0
                    maxHeight = Properties.standData.height minusPercent 52.0
                    minWidth = Properties.standData.width minusPercent 50.0

                    items = Properties.tests.map {
                        CheckBox(it.toString()).apply {
                            this.selectedProperty().onChange { isSelected ->
                                if (isSelected) {
                                    PreFillModel.selectedTests.add(it)
                                } else {
                                    PreFillModel.selectedTests.remove(it)
                                }
                            }
                            isWrapText = true
                            this@listview.widthProperty().onChange {
                                maxWidth = it - 20
                            }
                        }
                    }.observable()

                    fixedCellSize = 50.0
                    addEventFilter(MOUSE_PRESSED) { event ->
                        event.target.getChildList()?.getOrNull(0)?.let {
                            if (it is CheckBox) it.isSelected = !it.isSelected
                            event.consume()
                        }
                    }
                }
            }
//                }
//                tab("ПИ") {
//
//                }
//            }
            button("Начать испытания") {
                minWidth = 200.0
                prefWidth = 200.0
                maxWidth = 200.0
                maxHeight = 120.0
                prefHeight = 120.0
                maxHeight = 120.0
                isDefaultButton = true
                graphic = MaterialDesignIconView(MaterialDesignIcon.PLAY).apply {
                    glyphSize = 65
                    fill = c("green")
                }
                contentDisplay = ContentDisplay.TOP

                action {
                    showConfirmationTestObject(currentWindow, { controller.toTests() })
                }
            }
        }
    }

    fun resetComboBox() {
        testTypes.selectionModel.selectFirst()
        testTypes.selectionModel.select(-1)
    }

    fun refreshComboBox() {
        testTypes.items = Properties.testItems
    }
}
