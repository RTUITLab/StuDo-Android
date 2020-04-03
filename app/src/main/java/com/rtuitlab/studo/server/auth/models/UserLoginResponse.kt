package com.rtuitlab.studo.server.auth.models

import com.rtuitlab.studo.server.main.models.User

data class UserLoginResponse (
    val user : User,
    val accessToken : String
)