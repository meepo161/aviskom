package ru.avem.stand.modules.r.communication.model.devices.avem.latr

data class LatrControllerConfiguration(
    val minDuttyPercent: Float,
    val maxDuttyPercent: Float,
    val corridor: Float,
    val delta: Float,
    val timePulseMin: Int,
    val timePulseMax: Int,
    val voltageLimitMin: Float = 400f,
    val timeRegulation: Int = 60000
)
