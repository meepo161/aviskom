package ru.avem.stand.modules.r.tests.pi.overm

import javafx.beans.property.SimpleStringProperty
import ru.avem.stand.modules.i.TestModel


object OverMModel : TestModel() {
    val specifiedData = OverMData(descriptor = SimpleStringProperty("Заданные"))
    val measuredData = OverMData(descriptor = SimpleStringProperty("Измеренные"))
    val storedData = OverMData(descriptor = SimpleStringProperty("Сохранённые"))

    var specifiedU = 0.0
    var specifiedI = 0.0

    var specifiedCos = 0.0
    var specifiedEfficiency = 0.0
    var specifiedP = 0.0

    var specifiedRPM = 0.0
    var syncRPM = 0.0
    var specifiedF = 0.0
    var specifiedScheme = ""

    var specifiedOverMRatio = 1.0
    var specifiedOverMTestTime = 0.0

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
