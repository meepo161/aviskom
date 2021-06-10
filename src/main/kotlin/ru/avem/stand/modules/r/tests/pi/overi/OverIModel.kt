package ru.avem.stand.modules.r.tests.pi.overi

import javafx.beans.property.SimpleStringProperty
import ru.avem.stand.modules.i.TestModel


object OverIModel : TestModel() {
    val specifiedData = OverIData(descriptor = SimpleStringProperty("Заданные"))
    val measuredData = OverIData(descriptor = SimpleStringProperty("Измеренные"))
    val storedData = OverIData(descriptor = SimpleStringProperty("Сохранённые"))

    var specifiedU = 0.0
    var specifiedI = 0.0

    var specifiedCos = 0.0
    var specifiedEfficiency = 0.0
    var specifiedP = 0.0

    var specifiedRPM = 0.0
    var syncRPM = 0.0
    var specifiedF = 0.0
    var specifiedScheme = ""

    var specifiedOverIRatio = 1.0
    var specifiedOverITestTime = 0.0

    var specifiedM = 0.0

    @Volatile
    var measuredIA = 0.0

    @Volatile
    var measuredIB = 0.0

    @Volatile
    var measuredIC = 0.0

    @Volatile
    var measuredI = 0.0

    @Volatile
    var measuredU: Double = 0.0

    @Volatile
    var measuredP1 = 0.0

    @Volatile
    var measuredM = 0.0

    @Volatile
    var fLM = 0.0

    @Volatile
    var isLMDirectionRight = true

    @Volatile
    var isTIDirectionRight = true

    @Volatile
    var isContinueAgree = false
}
