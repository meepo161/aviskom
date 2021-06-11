package ru.avem.stand.modules.r.communication.model.devices.delta.danfoss

import ru.avem.kserialpooler.communication.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.communication.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.communication.utils.TransportException
import ru.avem.kserialpooler.communication.utils.TypeByteOrder
import ru.avem.kserialpooler.communication.utils.allocateOrderedByteBuffer
import ru.avem.stand.modules.r.communication.model.DeviceController
import ru.avem.stand.modules.r.communication.model.DeviceRegister
import ru.avem.stand.modules.r.communication.model.devices.delta.danfoss.DanfossModel.Companion.CONTROL_REGISTER
import ru.avem.stand.modules.r.communication.model.devices.delta.danfoss.DanfossModel.Companion.FREQ
import ru.avem.stand.modules.r.communication.model.devices.delta.danfoss.DanfossModel.Companion.FREQ_PERCENT
import ru.avem.stand.modules.r.communication.model.devices.delta.danfoss.DanfossModel.Companion.MAX_VOLTAGE
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Danfoss(
    override val name: String,
    override val protocolAdapter: ModbusRTUAdapter,
    override val id: Byte
) : DeviceController() {
    private val model = DanfossModel()
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()

    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    init {
        protocolAdapter.connection.connect()
    }

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                when (register.valueType) {
                    DeviceRegister.RegisterValueType.SHORT -> {
                        val modbusRegister =
                            protocolAdapter.readHoldingRegisters(id, register.address, 1).map(ModbusRegister::toShort)
                        register.value = modbusRegister.first().toDouble()
                    }
                    DeviceRegister.RegisterValueType.FLOAT -> {
                        val modbusRegister =
                            protocolAdapter.readHoldingRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        register.value =
                            allocateOrderedByteBuffer(modbusRegister, TypeByteOrder.BIG_ENDIAN, 4).float.toDouble()
                    }
                    DeviceRegister.RegisterValueType.INT32 -> {
                        val modbusRegister =
                            protocolAdapter.readHoldingRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        register.value =
                            allocateOrderedByteBuffer(modbusRegister, TypeByteOrder.BIG_ENDIAN, 4).int.toDouble()
                    }
                }
            }
            true
        } catch (e: TransportException) {
            false
        }
    }

    override fun readAllRegisters() {
        model.registers.values.forEach {
            readRegister(it)
        }
    }

    @Synchronized
    override fun <T : Number> writeRegister(register: DeviceRegister, value: T) {
        isResponding = try {
            when (value) {
                is Float -> {
                    val bb = ByteBuffer.allocate(4).putFloat(value).order(ByteOrder.BIG_ENDIAN)
                    val registers = listOf(ModbusRegister(bb.getShort(2)), ModbusRegister(bb.getShort(0)))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                }
                is Int -> {
                    val bb = ByteBuffer.allocate(4).putInt(value).order(ByteOrder.BIG_ENDIAN)
                    val registers = listOf(ModbusRegister(bb.getShort(2)), ModbusRegister(bb.getShort(0)))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                }
                is Short -> {
                    transactionWithAttempts {
                        protocolAdapter.presetSingleRegister(id, register.address, ModbusRegister(value))
                    }
                }
                else -> {
                    throw UnsupportedOperationException("Method can handle only with Float, Int and Short")
                }
            }
            true
        } catch (e: TransportException) {
            false
        }
    }

    override fun writeRegisters(register: DeviceRegister, values: List<Short>) {
        val registers = values.map { ModbusRegister(it) }
        transactionWithAttempts {
            protocolAdapter.presetMultipleRegisters(id, register.address, registers)
        }
    }

    override fun checkResponsibility() {
        try {
            model.registers.values.firstOrNull()?.let {
                readRegister(it)
            }
        } catch (ignored: TransportException) {
        }
    }

    override fun getRegisterById(idRegister: String) = model.getRegisterById(idRegister)

    enum class Direction {
        FORWARD,
        REVERSE
    }

    fun startObject() {
        protocolAdapter.forceSingleCoil(id,getRegisterById(CONTROL_REGISTER).value.toShort(),true)
    }

    fun stopObject() {
        protocolAdapter.forceSingleCoil(id,getRegisterById(CONTROL_REGISTER).value.toShort(),false)
    }

    fun setObjectParams(voltage: Number, percentF: Number) {
        try {
            writeRegister(getRegisterById(MAX_VOLTAGE), (voltage).toShort())
            writeRegister(getRegisterById(FREQ_PERCENT), (percentF.toInt()*100).toShort())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun Number.hz(): Short = (this.toDouble() * 100).toInt().toShort()
    private fun Number.v(): Short = (this.toDouble() * 10).toInt().toShort()

    fun setObjectUMax(voltageMax: Number) {
        writeRegister(getRegisterById(MAX_VOLTAGE), voltageMax.v())
    }

    fun setObjectFOut(fOut: Double) {
        writeRegister(getRegisterById(FREQ), fOut.hz())
    }
}