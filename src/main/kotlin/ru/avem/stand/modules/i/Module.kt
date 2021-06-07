package ru.avem.stand.modules.i

abstract class Module { // TODO нужна агрегация, а не наследование
    open val id: String = this::class.simpleName!!
    abstract val testModel: TestModel
}
