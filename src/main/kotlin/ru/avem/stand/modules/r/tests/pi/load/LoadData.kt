package ru.avem.stand.modules.r.tests.pi.load

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

data class LoadData(
    val descriptor: StringProperty,

    val tempAmb: StringProperty = SimpleStringProperty(""),
    val tempTI: StringProperty = SimpleStringProperty(""),
    val time: StringProperty = SimpleStringProperty(""),

    val U: StringProperty = SimpleStringProperty(""),
    val UMPTOY: StringProperty = SimpleStringProperty(""),
    val UMPTOV: StringProperty = SimpleStringProperty(""),
    val UAB: StringProperty = SimpleStringProperty(""),
    val UBC: StringProperty = SimpleStringProperty(""),
    val UCA: StringProperty = SimpleStringProperty(""),
    val I: StringProperty = SimpleStringProperty(""),
    val IA: StringProperty = SimpleStringProperty(""),
    val IB: StringProperty = SimpleStringProperty(""),
    val IC: StringProperty = SimpleStringProperty(""),
    val IMPTOY: StringProperty = SimpleStringProperty(""),
    val IMPTOV: StringProperty = SimpleStringProperty(""),
    val P1: StringProperty = SimpleStringProperty(""),
    val P2: StringProperty = SimpleStringProperty(""),
    val efficiency: StringProperty = SimpleStringProperty(""),
    val sk: StringProperty = SimpleStringProperty(""),

    val USGOV: StringProperty = SimpleStringProperty(""),
    val ISGOV: StringProperty = SimpleStringProperty(""),

    val torque: StringProperty = SimpleStringProperty(""),
    val RPM: StringProperty = SimpleStringProperty(""),

    val result: StringProperty = SimpleStringProperty(""),
)
