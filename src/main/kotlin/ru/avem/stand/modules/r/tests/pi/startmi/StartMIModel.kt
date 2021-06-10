package ru.avem.stand.modules.r.tests.pi.startmi

import javafx.beans.property.SimpleStringProperty
import ru.avem.stand.modules.i.TestModel


object StartMIModel : TestModel() {
    val specifiedData = StartMIData(descriptor = SimpleStringProperty("Заданные"))
    val measuredData = StartMIData(descriptor = SimpleStringProperty("Измеренные"))
    val storedData = StartMIData(descriptor = SimpleStringProperty("Сохранённые"))

    var specifiedU = 0.0
    var specifiedI = 0.0

    var specifiedCos = 0.0
    var specifiedEfficiency = 0.0
    var specifiedP = 0.0

    var specifiedRPM = 0.0
    var specifiedF = 0.0
    var specifiedScheme = ""

    @Volatile
    var measuredIA = 0.0

    @Volatile
    var measuredIB = 0.0

    @Volatile
    var measuredIC = 0.0

    @Volatile
    var measuredI = 0.0

    var measuredU: Double = 0.0

    var measuredTorqueMax: Double = 0.0
}
