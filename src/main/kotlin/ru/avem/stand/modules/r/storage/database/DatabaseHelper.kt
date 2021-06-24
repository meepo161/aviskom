package ru.avem.stand.modules.r.storage.database

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.stand.modules.i.tests.Test
import ru.avem.stand.modules.r.common.authorization.AuthorizationModel
import ru.avem.stand.modules.r.common.prefill.PreFillModel.serialNumberProp
import ru.avem.stand.modules.r.common.prefill.PreFillModel.testTypeProp
import ru.avem.stand.modules.r.storage.database.entities.*
import ru.avem.stand.modules.r.storage.testitemfields.TestItemFieldScheme
import ru.avem.stand.modules.r.storage.testitemfields.TypeEnterField
import ru.avem.stand.modules.r.storage.testitemfields.TypeFormatTestItemField
import java.sql.Connection
import java.text.SimpleDateFormat

fun validateDB() {
    Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    transaction {
        SchemaUtils.create(TestItems, TestItemFields, Reports, ReportFields)


        if (TestItem.all().count() == 0) {
            TestItem.new {
                name = "Объект испытания"
                level = 1
            }.also { ti ->
                createAsyncEngineTemplateBig().forEach {
                    TestItemField.new {
                        testItem = ti

                        key = it.key
                        title = it.title

                        typeEnterRaw = it.typeEnterRaw
                        typeFormatRaw = it.typeFormatRaw

                        minValue = it.minValue
                        value = it.value
                        maxValue = it.maxValue
                        unit = it.unit

                        permittedValuesString = it.permittedValuesString
                        permittedTitlesString = it.permittedTitlesString

                        blockName = it.blockName

                        isNotVoid = it.isNotVoid
                    }
                }
            }
        }
    }
}

private fun createAsyncEngineTemplateBig() = listOf(
    TestItemFieldScheme(
        key = "U",
        title = "Напряжение линейное",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "3000",
        maxValue = "3000",
        unit = "В",
        blockName = " МПТ"
    ),
    TestItemFieldScheme(
        key = "I",
        title = "Ток якоря",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "35.5",
        maxValue = "40",
        unit = "А",
        blockName = " МПТ"
    ),
    TestItemFieldScheme(
        key = "R_IKAS",
        title = "Сопротивление фазы статора при 20 °С",
        typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
        minValue = "0",
        value = "0.2",
        unit = "Ом",
        blockName = " МПТ"
    ),
    TestItemFieldScheme(
        key = "R_MGR",
        title = "Сопротивление изоляции",
        typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
        minValue = "0",
        value = "50",
        unit = "МОм",
        blockName = " МПТ"
    ),
    TestItemFieldScheme(
        key = "U_HV",
        title = "Напряжение ВИУ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "1000",
        maxValue = "10000",
        unit = "В",
        blockName = " МПТ"
    ),
    TestItemFieldScheme(
        key = "U_MGR",
        title = "Напряжение испытания мегаомметром",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "100",
        value = "1000",
        maxValue = "2500",
        unit = "В",
        blockName = " МПТ"
    ),
    TestItemFieldScheme(
        key = "I_HV",
        title = "Допустимый ток утечки ВИУ",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "0.5",
        maxValue = "1",
        unit = "А",
        blockName = " МПТ"
    ),
    TestItemFieldScheme(
        key = "T_HV",
        title = "Время испытания ВИУ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "60",
        maxValue = "3600",
        unit = "с",
        blockName = " МПТ"
    ),
    TestItemFieldScheme(
        key = "U_SG",
        title = "Напряжение линейное",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "230",
        maxValue = "230",
        unit = "В",
        blockName = " СГ"
    ),
    TestItemFieldScheme(
        key = "U_OV_SG",
        title = "Напряжение линейное ОВ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "230",
        maxValue = "230",
        unit = "В",
        blockName = " СГ"
    ),
    TestItemFieldScheme(
        key = "R_IKAS_SG",
        title = "Сопротивление фазы статора при 20 °С",
        typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
        minValue = "0",
        value = "0.2",
        unit = "Ом",
        blockName = " СГ"
    ),
    TestItemFieldScheme(
        key = "R_MGR_SG",
        title = "Сопротивление изоляции",
        typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
        minValue = "0",
        value = "50",
        unit = "МОм",
        blockName = " СГ"
    ),
    TestItemFieldScheme(
        key = "U_HV_SG",
        title = "Напряжение ВИУ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "1600",
        maxValue = "1600",
        unit = "В",
        blockName = " СГ"
    ),
    TestItemFieldScheme(
        key = "U_MGR_SG",
        title = "Напряжение испытания мегаомметром",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "100",
        value = "1000",
        maxValue = "2500",
        unit = "В",
        blockName = " СГ"
    ),
    TestItemFieldScheme(
        key = "I_HV_SG",
        title = "Допустимый ток утечки ВИУ",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "0.5",
        maxValue = "1",
        unit = "А",
        blockName = " СГ"
    ),
    TestItemFieldScheme(
        key = "T_HV_SG",
        title = "Время испытания ВИУ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "60",
        maxValue = "3600",
        unit = "с",
        blockName = " СГ"
    ),
    TestItemFieldScheme(
        key = "IDLE_TIME",
        title = "Время испытания ХХ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "60",
        unit = "с",
        blockName = "Общие"
    ),
    TestItemFieldScheme(
        key = "LOAD_TIME",
        title = "Время испытания НАГР",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "60",
        unit = "с",
        blockName = "Общие"
    )
)

fun saveProtocol(test: Test) = transaction {
    Report.new {
        serialNumber = serialNumberProp.value
        testType = testTypeProp.value.toString()
        this.test = test.name

        user1Name = AuthorizationModel.user0.login
        user2Name = AuthorizationModel.user1.login

        val millis = System.currentTimeMillis()
        date = SimpleDateFormat("dd.MM.yyyy").format(millis)
        time = SimpleDateFormat("HH:mm").format(millis)

        isSuccess = test.isSuccess.toString()

        template = test.reportTemplate
    }.also {
        ReportField.new {
            protocol = it
            key = "\$PROTOCOL_NUMBER\$"
            value = it.id.toString()
        }
        ReportField.new {
            protocol = it
            key = "\$TEST_TYPE\$"
            value = it.testType
        }
        ReportField.new {
            protocol = it
            key = "\$SERIAL_NUMBER\$"
            value = it.serialNumber
        }
        ReportField.new {
            protocol = it
            key = "\$TEST_NAME\$"
            value = it.test
        }
        test.reportFields.forEach { register ->
            ReportField.new {
                protocol = it
                key = "\$${register.key}\$"
                value = register.value
            }
        }
        ReportField.new {
            protocol = it
            key = "\$OPERATOR_NAME_1\$"
            value = it.user1Name
        }
        ReportField.new {
            protocol = it
            key = "\$OPERATOR_NAME_2\$"
            value = if (it.user2Name != "admin") it.user2Name else ""
        }
        ReportField.new {
            protocol = it
            key = "\$DATE\$"
            value = it.date
        }
        ReportField.new {
            protocol = it
            key = "\$TIME\$"
            value = it.time
        }
    }
}

fun getAllProtocols() = transaction { Report.all().toList() }

fun deleteProtocolByEntity(p: Report) {
    transaction {
        p.fields.forEach {
            it.delete()
        }
        p.delete()
    }
}

fun deleteProtocolById(id: EntityID<Int>) {
    transaction {
        Reports.deleteWhere {
            Reports.id eq id
        }
        ReportFields.deleteWhere {
            ReportFields.protocol eq id
        }
    }
}

fun deleteAllData() {
    transaction {
        Reports.deleteAll()
        ReportFields.deleteAll()
    }
}

fun createTestItem(name: String) = transaction {
    TestItem.new {
        this.name = name
        level = 1
    }.also { ti ->
        createAsyncEngineScheme().forEach {
            TestItemField.new {
                testItem = ti

                key = it.key
                title = it.title

                typeEnterRaw = it.typeEnterRaw
                typeFormatRaw = it.typeFormatRaw

                minValue = it.minValue
                value = it.value
                maxValue = it.maxValue
                unit = it.unit

                permittedValuesString = it.permittedValuesString
                permittedTitlesString = it.permittedTitlesString

                blockName = it.blockName

                isNotVoid = it.isNotVoid
            }
        }
    }
}

fun createAsyncEngineScheme() = listOf(
    TestItemFieldScheme(
        key = "U",
        title = "Напряжение линейное",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "3000",
        maxValue = "3000",
        unit = "В",
        blockName = " МПТ"
    ),
    TestItemFieldScheme(
        key = "I",
        title = "Ток якоря",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "35.5",
        maxValue = "40",
        unit = "А",
        blockName = " МПТ"
    ),
    TestItemFieldScheme(
        key = "R_IKAS",
        title = "Сопротивление фазы статора при 20 °С",
        typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
        minValue = "0",
        value = "0.2",
        unit = "Ом",
        blockName = " МПТ"
    ),
    TestItemFieldScheme(
        key = "R_MGR",
        title = "Сопротивление изоляции",
        typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
        minValue = "0",
        value = "50",
        unit = "МОм",
        blockName = " МПТ"
    ),
    TestItemFieldScheme(
        key = "U_HV",
        title = "Напряжение ВИУ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "1000",
        maxValue = "10000",
        unit = "В",
        blockName = " МПТ"
    ),
    TestItemFieldScheme(
        key = "U_MGR",
        title = "Напряжение испытания мегаомметром",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "100",
        value = "1000",
        maxValue = "2500",
        unit = "В",
        blockName = " МПТ"
    ),
    TestItemFieldScheme(
        key = "I_HV",
        title = "Допустимый ток утечки ВИУ",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "0.5",
        maxValue = "1",
        unit = "А",
        blockName = " МПТ"
    ),
    TestItemFieldScheme(
        key = "T_HV",
        title = "Время испытания ВИУ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "60",
        maxValue = "3600",
        unit = "с",
        blockName = " МПТ"
    ),
    TestItemFieldScheme(
        key = "U_SG",
        title = "Напряжение линейное",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "230",
        maxValue = "230",
        unit = "В",
        blockName = " СГ"
    ),
    TestItemFieldScheme(
        key = "U_OV_SG",
        title = "Напряжение линейное ОВ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "230",
        maxValue = "230",
        unit = "В",
        blockName = " СГ"
    ),
    TestItemFieldScheme(
        key = "R_IKAS_SG",
        title = "Сопротивление фазы статора при 20 °С",
        typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
        minValue = "0",
        value = "0.2",
        unit = "Ом",
        blockName = " СГ"
    ),
    TestItemFieldScheme(
        key = "R_MGR_SG",
        title = "Сопротивление изоляции",
        typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
        minValue = "0",
        value = "50",
        unit = "МОм",
        blockName = " СГ"
    ),
    TestItemFieldScheme(
        key = "U_HV_SG",
        title = "Напряжение ВИУ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "1600",
        maxValue = "1600",
        unit = "В",
        blockName = " СГ"
    ),
    TestItemFieldScheme(
        key = "U_MGR_SG",
        title = "Напряжение испытания мегаомметром",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "100",
        value = "1000",
        maxValue = "2500",
        unit = "В",
        blockName = " СГ"
    ),
    TestItemFieldScheme(
        key = "I_HV_SG",
        title = "Допустимый ток утечки ВИУ",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "0.5",
        maxValue = "1",
        unit = "А",
        blockName = " СГ"
    ),
    TestItemFieldScheme(
        key = "T_HV_SG",
        title = "Время испытания ВИУ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "60",
        maxValue = "3600",
        unit = "с",
        blockName = " СГ"
    ),
    TestItemFieldScheme(
        key = "IDLE_TIME",
        title = "Время испытания ХХ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "60",
        unit = "с",
        blockName = "Общие"
    ),
    TestItemFieldScheme(
        key = "LOAD_TIME",
        title = "Время испытания НАГР",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "60",
        unit = "с",
        blockName = "Общие"
    )
)

fun deleteTestItemByEntity(ti: TestItem) {
    transaction {
        ti.fieldsIterable.forEach {
            it.delete()
        }
        ti.delete()
    }
}

fun getAllTestItems() = transaction { TestItem.all().toList() }
