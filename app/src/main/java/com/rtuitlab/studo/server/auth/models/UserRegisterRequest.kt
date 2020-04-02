package com.rtuitlab.studo.server.auth.models

import com.google.gson.annotations.SerializedName

data class UserRegisterRequest(
    @SerializedName("firstname") val name : String,
    val surname : String,
    val email : String,
    val studentCardNumber : String,
    val password : String,
    val passwordConfirm : String
)