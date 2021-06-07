package ru.avem.stand.modules.i

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import ru.avem.stand.modules.r.tests.ProtectionsData
import ru.avem.stand.modules.r.tests.TestItemData

abstract class TestModel {
    open val progressProperty: DoubleProperty = SimpleDoubleProperty()


    val testItemData = TestItemData()
    val protections = ProtectionsData()
}
