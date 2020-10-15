package ru.rtuitlab.studo.server.general.users.models

data class ChangePasswordRequest(
    val id: String,
    val oldPassword: String,
    val newPassword: String
)