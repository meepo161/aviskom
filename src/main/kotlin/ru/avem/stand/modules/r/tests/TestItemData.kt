package ru.avem.stand.modules.r.tests

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

data class TestItemData(
    val U_Y_MPT         : StringProperty = SimpleStringProperty(""),
    val I_Y_MPT         : StringProperty = SimpleStringProperty(""),
    val R_IKAS_MPT      : StringProperty = SimpleStringProperty(""),
    val R_MGR_MPT       : StringProperty = SimpleStringProperty(""),
    val U_HV_MPT        : StringProperty = SimpleStringProperty(""),
    val U_MGR_MPT       : StringProperty = SimpleStringProperty(""),
    val I_HV_MPT        : StringProperty = SimpleStringProperty(""),
    val T_HV_MPT        : StringProperty = SimpleStringProperty(""),
    val U_Y_SG          : StringProperty = SimpleStringProperty(""),
    val U_V_SG          : StringProperty = SimpleStringProperty(""),
    val R_IKAS_SG       : StringProperty = SimpleStringProperty(""),
    val R_MGR_SG        : StringProperty = SimpleStringProperty(""),
    val U_HV_SG         : StringProperty = SimpleStringProperty(""),
    val U_MGR_SG        : StringProperty = SimpleStringProperty(""),
    val I_HV_SG         : StringProperty = SimpleStringProperty(""),
    val T_HV_SG         : StringProperty = SimpleStringProperty(""),
    val IDLE_TIME       : StringProperty = SimpleStringProperty(""),
    val LOAD_TIME       : StringProperty = SimpleStringProperty("")
)
