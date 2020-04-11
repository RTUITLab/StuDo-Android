package com.rtuitlab.studo.server.auth.models

import com.rtuitlab.studo.server.general.users.models.User

data class UserLoginResponse (
    val user : User,
    val accessToken : String
)