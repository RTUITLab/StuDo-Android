package ru.rtuitlab.studo.server.auth.models

data class UserLoginRequest (
    val email: String,
    val password: String
)