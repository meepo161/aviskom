package ru.avem.stand.modules.r.tests

import javafx.beans.property.SimpleStringProperty
import javafx.scene.paint.Color
import tornadofx.*

data class ProtectionsData(
    val overcurrentTI: Protection = Protection(),
    val overcurrentHV: Protection = Protection(),
    val doorsPEC: Protection = Protection(),
    val doorZone: Protection = Protection(),
    val PE: Protection = Protection(),
    val notPE: Protection = Protection(),
    val earthingSwitch: Protection = Protection(
        "НА ЗЕМЛЕ",
        c(168, 168, 168),
        "ВЫСОКОЕ НАПРЯЖЕНИЕ",
        c(118, 165, 175)
    ),
    val overheatingLM1: Protection = Protection(),
    val overheatingLM2: Protection = Protection(),
    val contactPosts: Protection = Protection(),
) {
    fun resetAll() {
        overcurrentTI.reset()
        overcurrentHV.reset()
        doorsPEC.reset()
        doorZone.reset()
        PE.reset()
        notPE.reset()
        earthingSwitch.reset()
        overheatingLM1.reset()
        overheatingLM2.reset()
        contactPosts.set()
    }
}

class Protection(
    private val notTriggeredValue: String = "В НОРМЕ",
    private val notTriggeredColor: Color = c(127, 255, 127),
    private val triggeredValue: String = "СРАБОТАЛА",
    private val triggeredColor: Color = c(255, 127, 127)
) {
    val prop = SimpleStringProperty(notTriggeredValue)

    var isTriggered: Boolean = false
        set(value) {
            field = value
            if (field) {
                prop.value = triggeredValue
            } else {
                prop.value = notTriggeredValue
            }
        }

    fun set() {
        isTriggered = true
    }

    fun reset() {
        isTriggered = false
    }

    fun color() = if (isTriggered) {
        triggeredColor
    } else {
        notTriggeredColor
    }
}
