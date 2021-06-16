package ru.avem.stand.modules.r.tests.pi.load

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

data class LoadData(
    val descriptor: StringProperty,

    val tempAmb: StringProperty = SimpleStringProperty(""),
    val tempTI: StringProperty = SimpleStringProperty(""),
    val T: StringProperty = SimpleStringProperty(""),

    val U: StringProperty = SimpleStringProperty(""),
    val UA: StringProperty = SimpleStringProperty(""),
    val UB: StringProperty = SimpleStringProperty(""),
    val UAB: StringProperty = SimpleStringProperty(""),
    val UBC: StringProperty = SimpleStringProperty(""),
    val UCA: StringProperty = SimpleStringProperty(""),
    val I: StringProperty = SimpleStringProperty(""),
    val IA: StringProperty = SimpleStringProperty(""),
    val IB: StringProperty = SimpleStringProperty(""),
    val IC: StringProperty = SimpleStringProperty(""),
    val I2A: StringProperty = SimpleStringProperty(""),
    val I2B: StringProperty = SimpleStringProperty(""),
    val cos: StringProperty = SimpleStringProperty(""),
    val P1: StringProperty = SimpleStringProperty(""),
    val P2: StringProperty = SimpleStringProperty(""),
    val efficiency: StringProperty = SimpleStringProperty(""),
    val sk: StringProperty = SimpleStringProperty(""),

    val torque: StringProperty = SimpleStringProperty(""),
    val RPM: StringProperty = SimpleStringProperty(""),

    val result: StringProperty = SimpleStringProperty(""),
)
