package com.rtuitlab.studo.server.general.profile.models

data class ChangeEmailRequest(
    val id : String,
    val oldEmail : String,
    val newEmail : String
)