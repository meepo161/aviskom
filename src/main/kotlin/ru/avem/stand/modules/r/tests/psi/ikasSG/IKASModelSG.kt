package ru.avem.stand.modules.r.tests.psi.ikasSG

import javafx.beans.property.SimpleStringProperty
import ru.avem.stand.modules.i.TestModel

object IKASModelSG : TestModel() {
    val specifiedData = IKASDataSG(descriptor = SimpleStringProperty("Заданные"))
    val measuredData = IKASDataSG(descriptor = SimpleStringProperty("Измеренные"))
    val calculatedData = IKASDataSG(descriptor = SimpleStringProperty("Рассчитанные фазные"))
    val calculatedR20Data = IKASDataSG(descriptor = SimpleStringProperty("Приведённые к 20℃"))
    val percentData = IKASDataSG(descriptor = SimpleStringProperty("% отношение к среднему"))

    var specifiedU = 0.0
    var specifiedI = 0.0

    var specifiedCos = 0.0
    var specifiedEfficiency = 0.0
    var specifiedP = 0.0

    var specifiedRPM = 0.0
    var specifiedF = 0.0
    var specifiedScheme = ""

    var specifiedR = 0.0

    @Volatile
    var status = 0

    @Volatile
    var measuredR = 0.0
}
