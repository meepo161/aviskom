package ru.avem.stand.modules.r.common.settings

import javafx.geometry.Side
import javafx.scene.control.TabPane
import ru.avem.stand.modules.r.common.authorization.AuthorizationModel
import ru.avem.stand.modules.r.common.settings.main.MainSettingsTab
import ru.avem.stand.modules.r.common.settings.users.UserSettingsView
import tornadofx.*

class StandSettingsTab : Workspace("Настройки", NavigationMode.Tabs) {
    private val controller: StandSettingsTabController by inject()

    private val mainSettingsTab: MainSettingsTab by inject()
    private val userSettingsView: UserSettingsView by inject()

    override fun onSave() {
        controller.serializeModel(mainSettingsTab.model)
        controller.serializeModel(userSettingsView.model)
    }

    override fun onDock() {
        tabContainer.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        tabContainer.side = Side.LEFT

        if (AuthorizationModel.user0.level >= 9) {
            dock<MainSettingsTab>()
        }
        dock<UserSettingsView>()

        header.items.remove(refreshButton)
        header.items.remove(deleteButton)
        header.items.remove(createButton)
        header.enableWhen(
            mainSettingsTab.model.dirty or
                    userSettingsView.model.dirty
        )

        tabContainer.selectionModel.select(0)
    }
}
