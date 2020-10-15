package ru.rtuitlab.studo.server.auth.models

import ru.rtuitlab.studo.server.general.users.models.User

data class UserLoginResponse (
    val user: User,
    val accessToken: String,
    val refreshToken: String
)