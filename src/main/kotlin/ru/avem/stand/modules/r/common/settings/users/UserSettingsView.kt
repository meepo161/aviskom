package ru.avem.stand.modules.r.common.settings.users

import javafx.geometry.Pos
import javafx.scene.control.ListView
import javafx.scene.control.PasswordField
import javafx.scene.layout.Priority
import ru.avem.stand.modules.r.storage.users.User
import tornadofx.*

class UserSettingsView : View("Пользователи") {
    private var listViewUsers: ListView<User> by singleAssign()
    private var repeatPasswordField: PasswordField by singleAssign()

    val model = UserItemViewModel()
    val validator = ValidationContext()

    override val root = anchorpane {
        hbox(spacing = 16.0) {
            anchorpaneConstraints {
                leftAnchor = 16.0
                rightAnchor = 16.0
                topAnchor = 16.0
                bottomAnchor = 16.0
            }
            vbox(spacing = 16.0) {
                listViewUsers = listview() {
                    vboxConstraints {
                        vGrow = Priority.ALWAYS
                    }
                    bindSelected(model)
                }
                hbox(spacing = 16.0) {
                    alignment = Pos.CENTER
                    button("Создать") {
                        action {
                            model.createUser()
                        }
                    }
                    button("Дублировать") {
                        action {
                            listViewUsers.selectedItem?.let {
                                model.cloneUser(it)
                                listViewUsers.selectionModel.selectLast()
                            }
                        }
                    }.disableWhen { model.empty }
                    button("Удалить") {
                        action {
                            listViewUsers.selectedItem?.let {
                                model.deleteUser(it)
                            }
                        }
                    }.disableWhen { model.empty }
                }
            }
            scrollpane {
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                }
                form {
                    paddingLeft = 32.0
                    prefWidth = 600.0
                    fieldset("               Аттрибуты пользователя") {
                        field("ФИО") {
                            textfield {
                                promptText = "Введите ФИО"
                                validator.addValidator(this) {
                                    if (it.isNullOrEmpty()) error("Обязательное поле") else null
                                }
                            }.bind(model.login)
                        }
                        field("Пароль") {
                            passwordfield {
                                promptText = "Введите пароль"
                                validator.addValidator(this) {
                                    if (it.isNullOrEmpty()) error("Обязательное поле") else null
                                }
                                textProperty().onChange {
                                    validator.validate(focusFirstError = false)
                                }
                            }.bind(model.password)
                        }
                        field("Повторите пароль") {
                            repeatPasswordField = passwordfield {
                                promptText = "Повторите ввод пароля"
                                validator.addValidator(this) {
                                    if (model.password.value != it) error("Пароли не совпадают") else null
                                }
                                textProperty().onChange {
                                    validator.validate(focusFirstError = false)
                                }
                                bind(model.repeatedPassword)
                            }
                        }
                    }
                }
            }
        }
    }
}
