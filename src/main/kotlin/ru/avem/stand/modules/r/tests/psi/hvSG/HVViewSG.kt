package ru.avem.stand.modules.r.tests.psi.hvSG

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.i.views.TestViewModule
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

class HVViewSG(title: String = "ВИУ", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: HVSG

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = HVSG()
        }
    }

    override fun EventTarget.testData() = vbox(spacing = 16) {
        padding = insets(8)

        hboxConstraints {
            hGrow = Priority.ALWAYS
        }

        label("Измеренные значения") {
            alignment = Pos.TOP_CENTER
            textAlignment = TextAlignment.CENTER
            useMaxWidth = true
            isWrapText = true
        }
        hbox {
            tableview(observableList(test.testModel.measuredData)) {
                minHeight = 64.0
                maxHeight = 64.0
                minWidth = 150.0
                prefWidth = 150.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)

                alignment = Pos.CENTER

                column("U, В", HVDataSG::U.getter)
                column("I, А", HVDataSG::I.getter)
                column("f, Гц", HVDataSG::F.getter)
            }
        }
        hbox {
            vboxConstraints {
                vGrow = Priority.ALWAYS
            }
            tableview(observableList(test.testModel.measuredData)) {
                minHeight = 64.0
                maxHeight = 64.0
                minWidth = 200.0
                prefWidth = 200.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)

                alignment = Pos.BOTTOM_RIGHT

                column("Время, с", HVDataSG::time.getter)
                column("Результат", HVDataSG::result.getter)
            }
        }
    }.addClass(Styles.paneBorders)
}
