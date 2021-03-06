package ru.avem.stand.modules.r.tests.pi.overi

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

data class OverIData(
    val descriptor: StringProperty,

    val T: StringProperty = SimpleStringProperty(""),

    val U: StringProperty = SimpleStringProperty(""),
    val UAB: StringProperty = SimpleStringProperty(""),
    val UBC: StringProperty = SimpleStringProperty(""),
    val UCA: StringProperty = SimpleStringProperty(""),
    val I: StringProperty = SimpleStringProperty(""),
    val IA: StringProperty = SimpleStringProperty(""),
    val IB: StringProperty = SimpleStringProperty(""),
    val IC: StringProperty = SimpleStringProperty(""),
    val cos: StringProperty = SimpleStringProperty(""),
    val P1: StringProperty = SimpleStringProperty(""),

    val torque: StringProperty = SimpleStringProperty(""),
    val RPM: StringProperty = SimpleStringProperty(""),

    val result: StringProperty = SimpleStringProperty(""),
)
