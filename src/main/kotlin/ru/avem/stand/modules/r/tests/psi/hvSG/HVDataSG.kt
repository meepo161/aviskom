package ru.avem.stand.modules.r.tests.psi.hvSG

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

data class HVDataSG(
    val U: StringProperty = SimpleStringProperty(""),
    val I: StringProperty = SimpleStringProperty(""),
    val F: StringProperty = SimpleStringProperty(""),

    val time: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty("")
)
