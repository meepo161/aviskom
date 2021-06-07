package ru.avem.stand.modules.r.communication.model

import ru.avem.kserialpooler.communication.Connection
import ru.avem.kserialpooler.communication.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.communication.utils.SerialParameters
import ru.avem.stand.modules.r.communication.adapters.serial.SerialAdapter
import ru.avem.stand.modules.r.communication.model.devices.avem.avem3.AVEM3
import ru.avem.stand.modules.r.communication.model.devices.avem.ikas.IKAS8
import ru.avem.stand.modules.r.communication.model.devices.avem.latr.AvemLatrController
import ru.avem.stand.modules.r.communication.model.devices.delta.c2000.C2000
import ru.avem.stand.modules.r.communication.model.devices.megaohmmeter.cs02021.CS02021
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.owen.trm202.TRM202
import ru.avem.stand.modules.r.communication.model.devices.satec.pm130.PM130
import java.lang.Thread.sleep
import kotlin.concurrent.thread

object CM {
    enum class DeviceID(description: String) {
        DD2("ОВЕН ПР102 - Программируемое реле"),
        PV23("АВЭМ-3-04(Iя)"),
        PV24("АВЭМ-4-01(Iя)"),
        PV25("АВЭМ-4-03 (Uов д)"),
        PV26("АВЭМ-4-03 (Uов г)"),
        PV27("АВЭМ-4-01 (Iов дв)"),
        PV28("АВЭМ-4-01 (Iов г )"),
        PAV41("Satec PM130-PLUS-P - Универсальный прибор"),
        PR61("АВЭМ ИКАС-8 - Измеритель DCR"),
        PR65("ЦС0202-1 - Меггер"),
        PS81("ТРМ202 - Термометр - (Вход 1 - окр. воздух | Вход 2 - ОИ вал)"),
        UZ91("Danfoss - Преобразователь частоты ОИ"),
        GV240("АРН-1-24-220")
    }

    private var isConnected = false

    private val mainConnection = Connection(
        adapterName = "CP2103 USB to RS-485",
        serialParameters = SerialParameters(8, 0, 1, 38400),
        timeoutRead = 100,
        timeoutWrite = 100
    ).apply {
        connect()
        isConnected = true
    }

    private val danfossConnection = Connection(
        adapterName = "CP2103 USB to RS-485-1",
        serialParameters = SerialParameters(8, 0, 1, 38400),
        timeoutRead = 100,
        timeoutWrite = 100
    ).apply {
        connect()
        isConnected = true
    }

    private val modbusAdapter = ModbusRTUAdapter(mainConnection)
    private val meggerAdapter = SerialAdapter(mainConnection)
    private val danfossAdapter = ModbusRTUAdapter(danfossConnection)

    private val devices: Map<DeviceID, IDeviceController> = mapOf(
        DeviceID.DD2 to PR(DeviceID.DD2.toString(), modbusAdapter, 2),
        DeviceID.PV23 to AVEM3(DeviceID.PV23.toString(), modbusAdapter, 23),
        DeviceID.PV24 to AVEM3(DeviceID.PV24.toString(), modbusAdapter, 24),
        DeviceID.PV25 to AVEM3(DeviceID.PV25.toString(), modbusAdapter, 25),
        DeviceID.PV26 to AVEM3(DeviceID.PV26.toString(), modbusAdapter, 26),
        DeviceID.PV27 to AVEM3(DeviceID.PV27.toString(), modbusAdapter, 27),
        DeviceID.PV28 to AVEM3(DeviceID.PV28.toString(), modbusAdapter, 28),
        DeviceID.PAV41 to PM130(DeviceID.PAV41.toString(), modbusAdapter, 41),
        DeviceID.PR61 to IKAS8(DeviceID.PR61.toString(), modbusAdapter, 61),
        DeviceID.PR65 to CS02021(DeviceID.PR65.toString(), meggerAdapter, 65),
        DeviceID.PS81 to TRM202(DeviceID.PS81.toString(), modbusAdapter, 81),
        DeviceID.UZ91 to C2000(DeviceID.UZ91.toString(), danfossAdapter, 91),
        DeviceID.GV240 to AvemLatrController(DeviceID.GV240.toString(), modbusAdapter, 240.toByte())
    )

    init {
        with(devices.values.groupBy { it.protocolAdapter.connection }) {
            keys.forEach { connection ->
                thread(isDaemon = true) {
                    while (true) {
                        if (isConnected) {
                            this[connection]!!.forEach {
                                it.readPollingRegisters()
                                sleep(1)
                            }
                        }
                        sleep(1)
                    }
                }
            }
        }
        thread(isDaemon = true) {
            while (true) {
                if (isConnected) {
                    devices.values.forEach {
                        it.writeWritingRegisters()
                        sleep(1)
                    }
                }
                sleep(1)
            }
        }
    }

    fun <T : IDeviceController> device(deviceID: DeviceID): T {
        return devices[deviceID] as T
    }

    fun startPoll(deviceID: DeviceID, registerID: String, block: (Number) -> Unit) {
        val device = device<IDeviceController>(deviceID)
        val register = device.getRegisterById(registerID)
        register.addObserver { _, arg ->
            block(arg as Number)
        }
        device.addPollingRegister(register)
    }

    fun clearPollingRegisters() {
        devices.values.forEach(IDeviceController::removeAllPollingRegisters)
        devices.values.forEach(IDeviceController::removeAllWritingRegisters)
    }

    fun removePollingRegister(deviceID: DeviceID, registerID: String) {
        val device = device<IDeviceController>(deviceID)
        val register = device.getRegisterById(registerID)
        register.deleteObservers()
        device.removePollingRegister(register)
    }

    fun checkDevices(checkedDevices: List<IDeviceController>): List<DeviceID> {
        checkedDevices.forEach(IDeviceController::checkResponsibility)
        return listOfUnresponsiveDevices(checkedDevices)
    }

    fun listOfUnresponsiveDevices(checkedDevices: List<IDeviceController>) =
        devices.filter { checkedDevices.toList().contains(it.value) && !it.value.isResponding }.keys.toList()

    fun addWritingRegister(deviceID: DeviceID, registerID: String, value: Number) {
        val device = device<IDeviceController>(deviceID)
        val register = device.getRegisterById(registerID)
        device.addWritingRegister(register to value)
    }
}
