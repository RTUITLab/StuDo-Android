package com.rtuitlab.studo.server.general.profile.models

data class ChangePasswordRequest(
    val id: String,
    val oldPassword: String,
    val newPassword: String
)