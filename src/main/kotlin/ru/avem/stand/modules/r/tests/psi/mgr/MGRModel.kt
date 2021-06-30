package ru.avem.stand.modules.r.tests.psi.mgr

import ru.avem.stand.modules.i.TestModel

object MGRModel : TestModel() {
    val specifiedData = MGRData()
    val measuredData = MGRData()

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

    var latrStatus = 0
}
