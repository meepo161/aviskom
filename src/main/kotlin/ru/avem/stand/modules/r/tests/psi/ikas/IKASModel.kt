package ru.avem.stand.modules.r.tests.psi.ikas

import javafx.beans.property.SimpleStringProperty
import ru.avem.stand.modules.i.TestModel

object IKASModel : TestModel() {
    val specifiedData = IKASData(descriptor = SimpleStringProperty("Заданные"))
    val measuredData = IKASData(descriptor = SimpleStringProperty("Измеренные"))
    val calculatedData = IKASData(descriptor = SimpleStringProperty("Рассчитанные фазные"))
    val calculatedR20Data = IKASData(descriptor = SimpleStringProperty("Приведённые к 20℃"))
    val percentData = IKASData(descriptor = SimpleStringProperty("% отношение к среднему"))


    var specifiedU_Y_MPT = 0.0
    var specifiedI_Y_MPT = 0.0
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

    var specifiedR = 0.0

    @Volatile
    var status = 0

    @Volatile
    var measuredR = 0.0
}
