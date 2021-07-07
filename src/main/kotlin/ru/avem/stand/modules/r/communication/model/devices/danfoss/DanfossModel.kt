package ru.avem.stand.modules.r.communication.model.devices.danfoss

import ru.avem.stand.modules.r.communication.model.DeviceRegister
import ru.avem.stand.modules.r.communication.model.IDeviceModel

class DanfossModel : IDeviceModel {
    companion object {
        const val MAX_VOLTAGE = "MAX_VOLTAGE"
        const val FREQ = "FREQ"
        const val FREQ_PERCENT = "FREQ_PERCENT"

        const val NORMALLY_MASK = "NORMALLY_MASK" // 0 - NO, 1 - NC
        const val CLOSURE_MASK = "CLOSURE_MASK"
        const val CURRENT = "CURRENT"
        const val VOLTAGE = "VOLTAGE"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        MAX_VOLTAGE to DeviceRegister(1219, DeviceRegister.RegisterValueType.SHORT),
        FREQ to DeviceRegister(1229, DeviceRegister.RegisterValueType.SHORT),
        FREQ_PERCENT to DeviceRegister(3099, DeviceRegister.RegisterValueType.SHORT),

        NORMALLY_MASK to DeviceRegister(0x0409, DeviceRegister.RegisterValueType.SHORT),
        CLOSURE_MASK to DeviceRegister(0x041A, DeviceRegister.RegisterValueType.SHORT),

        CURRENT to DeviceRegister(16139, DeviceRegister.RegisterValueType.INT32),
        VOLTAGE to DeviceRegister(16119, DeviceRegister.RegisterValueType.SHORT)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
