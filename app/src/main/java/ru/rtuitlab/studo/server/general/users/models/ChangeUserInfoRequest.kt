package ru.rtuitlab.studo.server.general.users.models

import com.google.gson.annotations.SerializedName

data class ChangeUserInfoRequest(
    val id : String,
    @SerializedName("firstname") val name : String,
    val surname : String,
    val studentCardNumber : String
)