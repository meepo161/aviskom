package ru.avem.stand.modules.r.tests.psi.idle

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

data class IdleData(
    val descriptor: StringProperty,

    val U_Y_MPT: StringProperty = SimpleStringProperty(""),
    val U_V_MPT: StringProperty = SimpleStringProperty(""),
    val I_Y_MPT: StringProperty = SimpleStringProperty(""),
    val I_V_MPT: StringProperty = SimpleStringProperty(""),
    val F: StringProperty = SimpleStringProperty(""),

    val tempAmb: StringProperty = SimpleStringProperty(""),
    val tempTI: StringProperty = SimpleStringProperty(""),

    val time: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty(""),
)
