package ru.avem.stand.modules.r.communication.model.devices.owen.pr

import ru.avem.kserialpooler.communication.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.communication.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.communication.utils.TransportException
import ru.avem.stand.modules.r.communication.model.DeviceController
import ru.avem.stand.modules.r.communication.model.DeviceRegister
import java.lang.Thread.sleep
import java.nio.ByteBuffer
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.pow

class PR(
    override val name: String,
    override val protocolAdapter: ModbusRTUAdapter,
    override val id: Byte
) : DeviceController() {
    val model = PRModel()
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()

    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    private var outMask01To16: Short = 0

    fun init() {
        writeRegister(getRegisterById(PRModel.WD_TIMEOUT), 8000.toShort())

        resetTriggers()

        writeRegister(getRegisterById(PRModel.DO_01_16_ERROR_S1_MASK_0), 0xFFFF.toShort())
        writeRegister(getRegisterById(PRModel.DO_01_16_ERROR_S1_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DO_17_32_ERROR_S1_MASK_0), 0xFFFF.toShort())
        writeRegister(getRegisterById(PRModel.DO_17_32_ERROR_S1_MASK_1), 0x0000.toShort())

        writeRegister(getRegisterById(PRModel.DI_01_16_ERROR_MASK_1), 0x0C01.toShort())
        writeRegister(getRegisterById(PRModel.DI_01_16_ERROR_MASK_0), 0x0098.toShort())
        writeRegister(getRegisterById(PRModel.DI_17_32_ERROR_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_17_32_ERROR_MASK_0), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_33_48_ERROR_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_33_48_ERROR_MASK_0), 0x0000.toShort())

        writeRegister(getRegisterById(PRModel.CMD), 3.toShort()) // RESET ERROR + WD_CYCLE
    }

    fun resetTriggers() {
        writeRegister(getRegisterById(PRModel.DI_01_16_RST), 0xFFFF.toShort())
        writeRegister(getRegisterById(PRModel.DI_01_16_RST), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_17_32_RST), 0xFFFF.toShort())
        writeRegister(getRegisterById(PRModel.DI_17_32_RST), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_33_48_RST), 0xFFFF.toShort())
        writeRegister(getRegisterById(PRModel.DI_33_48_RST), 0x0000.toShort())
    }

    fun initWithoutProtections() {
        writeRegister(getRegisterById(PRModel.WD_TIMEOUT), 8000.toShort())

        resetTriggers()

        writeRegister(getRegisterById(PRModel.DO_01_16_ERROR_S1_MASK_0), 0xFFFF.toShort())
        writeRegister(getRegisterById(PRModel.DO_01_16_ERROR_S1_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DO_17_32_ERROR_S1_MASK_0), 0xFFFF.toShort())
        writeRegister(getRegisterById(PRModel.DO_17_32_ERROR_S1_MASK_1), 0x0000.toShort())

        writeRegister(getRegisterById(PRModel.DI_01_16_ERROR_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_01_16_ERROR_MASK_0), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_17_32_ERROR_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_17_32_ERROR_MASK_0), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_33_48_ERROR_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_33_48_ERROR_MASK_0), 0x0000.toShort())

        writeRegister(getRegisterById(PRModel.CMD), 3.toShort()) // RESET ERROR + WD_CYCLE
    }

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                val modbusRegister =
                    protocolAdapter.readHoldingRegisters(id, register.address, 1).map(ModbusRegister::toShort)
                register.value = modbusRegister.first()
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
                    val bb = ByteBuffer.allocate(4).putFloat(value)
                    val registers = listOf(ModbusRegister(bb.getShort(2)), ModbusRegister(bb.getShort(0)))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                }
                is Short -> {
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, listOf(ModbusRegister(value)))
                    }
                }
                else -> {
                    throw UnsupportedOperationException("Method can handle only with Float and Short")
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

    override fun checkResponsibility() {
        model.registers.values.firstOrNull()?.let {
            readRegister(it)
        }
    }

    override fun getRegisterById(idRegister: String) = model.getRegisterById(idRegister)

    fun setUOnTM1(voltage: Float) {
        writeRegister(getRegisterById(PRModel.AO_01), voltage)
    }

    fun setUOnTM2(voltage: Float) {
        writeRegister(getRegisterById(PRModel.AO_02), voltage)
    }

    fun onIkasKM61() {
        onOutput01To16(1)
    }

    fun onViuArnKM30() {
        onOutput01To16(2)
    }

    fun onKM1() {
        onOutput01To16(3)
    }

    fun onRotateKM2() {
        onOutput01To16(4)
    }

    fun onUNM55KM81() {
        onOutput01To16(5)
    }

    fun onUNM15KM82() {
        onOutput01To16(6)
    }

    fun onOtvodK30() {
        onOutput01To16(7)
    }

    fun onRotateUNMKKM81() {
        onOutput01To16(8)
    }

    fun signalize() {
        onOutput01To16(9)
        onOutput01To16(10)
        sleep(3000)
        offOutput01To16(10)
    }

    fun onShuntirGB30() {
        onOutput01To16(11)
    }

    fun onVIUQV1() {
        onOutput01To16(12)
    }

    fun onMGRQV2() {
        onOutput01To16(13)
    }

    fun onPEQV3() {
        onOutput01To16(14)
    }

    fun offIkasKM61() {
        offOutput01To16(1)
    }

    fun offViuArnKM30() {
        offOutput01To16(2)
    }

    fun offKM1() {
        offOutput01To16(3)
    }

    fun offRotateKM2() {
        offOutput01To16(4)
    }

    fun offUNM55KM81() {
        offOutput01To16(5)
    }

    fun offUNM15KM82() {
        offOutput01To16(6)
    }

    fun offOtvodK30() {
        offOutput01To16(7)
    }

    fun offRotateUNMKKM81() {
        offOutput01To16(8)
    }

    fun offSignalize() {
        offOutput01To16(9)
        offOutput01To16(10)
    }

    fun offShuntirGB30() {
        offOutput01To16(11)
    }

    fun offVIUQV1() {
        offOutput01To16(12)
    }

    fun offMGRQV2() {
        offOutput01To16(13)
    }

    fun offPEQV3() {
        offOutput01To16(14)
    }

    fun offAllKMs() {
        outMask01To16 = 0
        writeRegister(getRegisterById(PRModel.DO_01_16), outMask01To16)
    }

    private fun onOutput01To16(position: Short) {
        val bitPosition = position - 1
        outMask01To16 = outMask01To16 or 2.0.pow(bitPosition).toInt().toShort()
        writeRegister(getRegisterById(PRModel.DO_01_16), outMask01To16)
    }

    private fun offOutput01To16(position: Short) {
        val bitPosition = position - 1
        outMask01To16 = outMask01To16 and 2.0.pow(bitPosition).toInt().inv().toShort()
        writeRegister(getRegisterById(PRModel.DO_01_16), outMask01To16)
    }
}
