package ru.avem.stand.modules.r.tests.psi.mgrSG

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

data class MGRDataSG(
    val U: StringProperty = SimpleStringProperty(""),
    val R15: StringProperty = SimpleStringProperty(""),
    val R60: StringProperty = SimpleStringProperty(""),
    val K_ABS: StringProperty = SimpleStringProperty(""),
    val tempAmb: StringProperty = SimpleStringProperty(""),
    val tempTI: StringProperty = SimpleStringProperty(""),

    val result: StringProperty = SimpleStringProperty("")
)
