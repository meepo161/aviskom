package ru.avem.stand.modules.r.tests.pi.load

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

data class LoadData(
    val descriptor: StringProperty,

    val U_Y_MPT: StringProperty = SimpleStringProperty(""),
    val U_V_MPT: StringProperty = SimpleStringProperty(""),
    val I_Y_MPT: StringProperty = SimpleStringProperty(""),
    val I_V_MPT: StringProperty = SimpleStringProperty(""),

    val U: StringProperty = SimpleStringProperty(""),
    val UAB: StringProperty = SimpleStringProperty(""),
    val UBC: StringProperty = SimpleStringProperty(""),
    val UCA: StringProperty = SimpleStringProperty(""),
    val I: StringProperty = SimpleStringProperty(""),
    val IA: StringProperty = SimpleStringProperty(""),
    val IB: StringProperty = SimpleStringProperty(""),
    val IC: StringProperty = SimpleStringProperty(""),
    val P1: StringProperty = SimpleStringProperty(""),
    val U_V_SG: StringProperty = SimpleStringProperty(""),
    val I_V_SG: StringProperty = SimpleStringProperty(""),
    val F: StringProperty = SimpleStringProperty(""),

    val tempAmb: StringProperty = SimpleStringProperty(""),
    val tempTI: StringProperty = SimpleStringProperty(""),

    val time: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty(""),
)
