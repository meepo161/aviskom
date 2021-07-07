package ru.avem.stand.modules.r.tests.psi.idle

import javafx.beans.property.SimpleStringProperty
import ru.avem.stand.modules.i.TestModel

object IdleModel : TestModel() {
    val specifiedData = IdleData(descriptor = SimpleStringProperty("Заданные"))
    val measuredData = IdleData(descriptor = SimpleStringProperty("Измеренные"))
    val storedData = IdleData(descriptor = SimpleStringProperty("Сохранённые"))

    var specifiedU_Y_MPT = 0.0
    var specifiedI_Y_MPT = 0.0
    var specifiedU_V_MPT = 0.0
    var specifiedI_V_MPT = 0.0
    var specifiedR_IKAS_MPT = 0.0
    var specifiedR_MGR_MPT = 0.0
    var specifiedU_HV_MPT = 0.0
    var specifiedU_MGR_MPT = 0.0
    var specifiedI_HV_MPT = 0.0
    var specifiedT_HV_MPT = 0.0
    var specifiedU_Y_SG = 0.0
    var specifiedU_V_SG = 0.0
    var specifiedR_IKAS_SG = 0.0
    var specifiedR_MGR_SG = 0.0
    var specifiedU_HV_SG = 0.0
    var specifiedU_MGR_SG = 0.0
    var specifiedI_HV_SG = 0.0
    var specifiedT_HV_SG = 0.0
    var specifiedIDLE_TIME = 0.0
    var specifiedLOAD_TIME = 0.0

    @Volatile
    var measuredU_Y_MPT = 0.0

    @Volatile
    var measuredU_V_MPT = 0.0

    @Volatile
    var measuredI_Y_MPT = 0.0

    @Volatile
    var measuredI_V_MPT = 0.0

    @Volatile
    var measuredF = 0.0

    @Volatile
    var measuredUC = 0.0

    @Volatile
    var measuredIC = 0.0

    @Volatile
    var measuredI = 0.0

    var measuredU: Double = 0.0
}
