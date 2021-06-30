package ru.avem.stand.modules.r.tests.psi.ikasSG

import javafx.beans.property.SimpleStringProperty
import ru.avem.stand.modules.i.TestModel

object IKASModelSG : TestModel() {
    val specifiedData = IKASDataSG(descriptor = SimpleStringProperty("Заданные"))
    val measuredData = IKASDataSG(descriptor = SimpleStringProperty("Измеренные"))
    val calculatedData = IKASDataSG(descriptor = SimpleStringProperty("Рассчитанные фазные"))
    val calculatedR20Data = IKASDataSG(descriptor = SimpleStringProperty("Приведённые к 20℃"))
    val percentData = IKASDataSG(descriptor = SimpleStringProperty("% отношение к среднему"))

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

    @Volatile
    var status = 0

    @Volatile
    var measuredR = 0.0
}
