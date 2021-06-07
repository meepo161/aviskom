package ru.avem.stand.modules.r.tests.psi.idle

import javafx.beans.property.SimpleStringProperty
import ru.avem.stand.modules.i.TestModel

object IdleModel : TestModel() {
    val specifiedData = IdleData(descriptor = SimpleStringProperty("Заданные"))
    val measuredData = IdleData(descriptor = SimpleStringProperty("Измеренные"))
    val storedData = IdleData(descriptor = SimpleStringProperty("Сохранённые"))

    var specifiedU = 0.0
    var specifiedI = 0.0

    var specifiedCos = 0.0
    var specifiedEfficiency = 0.0
    var specifiedP = 0.0

    var specifiedRPM = 0.0
    var specifiedF = 0.0
    var specifiedScheme = ""

    var specifiedIdleI = 0.0
    var specifiedIdleTestTime = 0.0

    @Volatile
    var measuredUA = 0.0

    @Volatile
    var measuredUB = 0.0

    @Volatile
    var measuredUC = 0.0

    @Volatile
    var measuredIA = 0.0

    @Volatile
    var measuredIB = 0.0

    @Volatile
    var measuredIC = 0.0

    @Volatile
    var measuredI = 0.0

    var measuredU: Double = 0.0
}
