package ru.avem.stand.modules.r.tests.psi.mgrSG

import ru.avem.stand.modules.i.TestModel

object MGRModelSG : TestModel() {
    val specifiedData = MGRDataSG()
    val measuredData = MGRDataSG()

    var specifiedU = 0.0
    var specifiedI = 0.0

    var specifiedCos = 0.0
    var specifiedEfficiency = 0.0
    var specifiedP = 0.0

    var specifiedRPM = 0.0
    var specifiedF = 0.0
    var specifiedScheme = ""

    var specifiedUMGR = 0.0
    var specifiedRMGR = 0.0
}
