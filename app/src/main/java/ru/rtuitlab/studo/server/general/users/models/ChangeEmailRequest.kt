package ru.rtuitlab.studo.server.general.users.models

data class ChangeEmailRequest(
    val id : String,
    val oldEmail : String,
    val newEmail : String
)