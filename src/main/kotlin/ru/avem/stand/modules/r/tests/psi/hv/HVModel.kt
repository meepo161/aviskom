package ru.avem.stand.modules.r.tests.psi.hv

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import ru.avem.stand.modules.i.TestModel
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.*
import kotlin.math.abs

object HVModel : TestModel() {
    override val progressProperty: DoubleProperty = SimpleDoubleProperty().also {
        it.onChange {
            measuredData.time.value = "%.0f".format(abs(it * specifiedT))
        }
    }

    val specifiedData = HVData()
    val measuredData = HVData()
    val storedData = HVData()
    val initData = HVData()

    var specifiedU = 0.0
    var specifiedI = 0.0

    var specifiedCos = 0.0
    var specifiedEfficiency = 0.0
    var specifiedP = 0.0

    var specifiedRPM = 0.0
    var specifiedF = 0.0
    var specifiedScheme = ""

    var specifiedUHV = 0.0
    var specifiedIHV = 0.0
    var specifiedT = 0.0

    var measuredU: Double = 0.0
    var measuredI: Double = 0.0

    var lastFIP1U = 0.0
}
