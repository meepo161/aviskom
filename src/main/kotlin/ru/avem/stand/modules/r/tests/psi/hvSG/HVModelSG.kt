package ru.avem.stand.modules.r.tests.psi.hvSG

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import ru.avem.stand.modules.i.TestModel
import tornadofx.*
import kotlin.math.abs

object HVModelSG : TestModel() {
    override val progressProperty: DoubleProperty = SimpleDoubleProperty().also {
        it.onChange {
            measuredData.time.value = "%.0f".format(abs(it * specifiedT_HV_SG))
        }
    }

    val specifiedData = HVDataSG()
    val measuredData = HVDataSG()
    val storedData = HVDataSG()
    val initData = HVDataSG()


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

    var measuredU: Double = 0.0
    var measuredI: Double = 0.0

    var lastFIP1U = 0.0
}
