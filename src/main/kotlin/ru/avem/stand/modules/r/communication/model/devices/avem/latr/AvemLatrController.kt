package ru.avem.stand.modules.r.communication.model.devices.avem.latr

import ru.avem.kserialpooler.communication.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.communication.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.communication.utils.TransportException
import ru.avem.kserialpooler.communication.utils.TypeByteOrder
import ru.avem.kserialpooler.communication.utils.allocateOrderedByteBuffer
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.DeviceController
import ru.avem.stand.modules.r.communication.model.DeviceRegister
import ru.avem.stand.modules.r.communication.model.devices.LatrStuckException
import java.lang.Thread.sleep
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AvemLatrController(
    override val name: String,
    override val protocolAdapter: ModbusRTUAdapter,
    override val id: Byte,
): DeviceController() {
    val model = AvemLatrModel()
    override var isResponding = false
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()
//    override val pollingMutex = Any() TODO проверить с Меггером
//    override val writingMutex = Any()
    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    companion object {
        const val LATR_RESETER: Short = 0x5A5A
    }

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                when (register.valueType) {
                    DeviceRegister.RegisterValueType.SHORT -> {
                        val value =
                            protocolAdapter.readInputRegisters(id, register.address, 1).first().toShort()
                        register.value = value
                    }
                    DeviceRegister.RegisterValueType.FLOAT -> {
                        val modbusRegister =
                            protocolAdapter.readInputRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        register.value =
                            allocateOrderedByteBuffer(modbusRegister, TypeByteOrder.BIG_ENDIAN, 4).float
                    }
                    DeviceRegister.RegisterValueType.INT32 -> {
                        val modbusRegister =
                            protocolAdapter.readInputRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        register.value =
                            allocateOrderedByteBuffer(modbusRegister, TypeByteOrder.BIG_ENDIAN, 4).int
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

    override fun <T : Number> writeRegister(register: DeviceRegister, value: T) {
        isResponding = try {
            when (value) {
                is Float -> {
                    val bb = ByteBuffer.allocate(4).putFloat(value).order(ByteOrder.BIG_ENDIAN)
                    val registers = listOf(ModbusRegister(bb.getShort(0)), ModbusRegister(bb.getShort(2)))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                }
                is Int -> {
                    val bb = ByteBuffer.allocate(4).putInt(value).order(ByteOrder.BIG_ENDIAN)
                    val registers = listOf(ModbusRegister(bb.getShort(0)), ModbusRegister(bb.getShort(2)))
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
        isResponding = try {
            transactionWithAttempts {
                protocolAdapter.presetMultipleRegisters(id, register.address, registers)
            }
            true
        } catch (e: TransportException) {
            false
        }
    }

    override fun getRegisterById(idRegister: String) = model.getRegisterById(idRegister)

    override fun checkResponsibility() {
        model.registers.values.firstOrNull()?.let {
            readRegister(it)
        }
    }

    fun reset(block: (() -> Unit)? = null, deviceID: CM.DeviceID) {
        CM.startPoll(deviceID, AvemLatrModel.IR_LIMIT_SWITCH) {}
        with(getRegisterById(AvemLatrModel.IR_RESET)) {
            writeRegister(this, LATR_RESETER)
        }
        sleep(2000)
        var timeout = 50
        while ((getRegisterById(AvemLatrModel.IR_LIMIT_SWITCH).value.toShort() != 2.toShort()) && --timeout >= 0) {
            sleep(1000)
            if (timeout <= 47) {
                block?.let {
                    it()
                }
            }
        }
        CM.removePollingRegister(deviceID, AvemLatrModel.IR_LIMIT_SWITCH)
        if (timeout < 0) {
            throw LatrStuckException()
        }
    }

    fun presetParameters(testObject: LatrControllerConfiguration) {
        writeRegister(getRegisterById(AvemLatrModel.IR_DUTY_MIN_PERCENT), testObject.minDuttyPercent)
        writeRegister(getRegisterById(AvemLatrModel.IR_DUTY_MAX_PERCENT), testObject.maxDuttyPercent)
        writeRegister(getRegisterById(AvemLatrModel.IR_TIME_PULSE_MIN), testObject.timePulseMin)
        writeRegister(getRegisterById(AvemLatrModel.IR_TIME_PULSE_MAX), testObject.timePulseMax)
        writeRegister(getRegisterById(AvemLatrModel.IR_CORRIDOR), testObject.corridor)
        writeRegister(getRegisterById(AvemLatrModel.IR_DELTA), testObject.delta)
        writeRegister(getRegisterById(AvemLatrModel.IR_VOLTAGE_LIMIT_MIN), testObject.voltageLimitMin)
        writeRegister(getRegisterById(AvemLatrModel.IR_TIME_REGULATION), testObject.timeRegulation)
    }

    fun start(voltage: Float) {
        writeRegister(getRegisterById(AvemLatrModel.IR_SET_VALUE), voltage)
        writeRegister(getRegisterById(AvemLatrModel.IR_START), 1.toShort())
        writeRegister(getRegisterById(AvemLatrModel.IR_RESET), 0.toShort())
    }

    fun stop() {
        writeRegister(getRegisterById(AvemLatrModel.IR_START), 0.toShort())
        writeRegister(getRegisterById(AvemLatrModel.IR_RESET), 1.toShort())
    }

    fun plusVoltage() {
        start(250f)
    }

    fun minusVoltage() {
        start(1f)
    }
}
