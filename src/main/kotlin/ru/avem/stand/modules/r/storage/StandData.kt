package ru.avem.stand.modules.r.storage

import java.nio.file.Path
import java.nio.file.Paths

class StandData {
    val width: Double = 1024.0
    val height: Double = 768.0
    val isMaximized: Boolean = true
    val isResizable: Boolean = true
    val isFullScreen: Boolean = false
    val splashPicture: Path = Paths.get("splash.png")
    val splashTime: Long = 0L
    val icon: Path = Paths.get("icon.png")
    val textSize: Int = 18
    val exitConfirmation: Boolean = true

    val customer: String = "«АВИСКОМ»"
    val customerPlace: String = "г. Омск"

    val aiel: String = "АИЕЛ.441462" //TODO
    val serialNumber: String = "210560138" //TODO

    val manufacture: String = "ООО «Авиаагрегат-Н»"
    val manufacturePlace: String = "г. Новочеркасск"
    val manufactureDate: String = "2021 г."

    val titleFull: String = "Комплексный стенд проверки электрических машин"
    val titleShort: String = "Стенд для испытаний преобразователя 1ПВ7"

    val hardwareID: String = ""
    val softwareID: String = ""

    val isLogin0Enabled: Boolean = true
    val login1Title: String = "Испытатель"
    val login1Level: Int = 0

    val isLogin1Enabled: Boolean = false
    val login2Title: String = "Руководитель"
    val login2Level: Int = 0
}
