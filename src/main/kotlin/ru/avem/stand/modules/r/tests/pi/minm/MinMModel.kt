package ru.avem.stand.modules.r.tests.pi.minm

import javafx.beans.property.SimpleStringProperty
import ru.avem.stand.modules.i.TestModel


object MinMModel : TestModel() {
    val specifiedData = MinMData(descriptor = SimpleStringProperty("Заданные"))
    val measuredData = MinMData(descriptor = SimpleStringProperty("Измеренные"))
    val storedData = MinMData(descriptor = SimpleStringProperty("Сохранённые"))

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

    @Volatile
    var measuredU: Double = 0.0

    @Volatile
    var isNeedAbsTorque = false

    @Volatile
    var measuredTorque = 0.0

    @Volatile
    var measuredTorqueMin = 0.0

    @Volatile
    var measuredTorqueMax = 0.0

    @Volatile
    var isLMDirectionRight = true

    @Volatile
    var isTIDirectionRight = true

    enum class ResultBlob {
        UNDEFINED,
        NEED_MORE,
        FOUND,
        NEED_LESS
    }

    var result: ResultBlob = ResultBlob.NEED_MORE
}
