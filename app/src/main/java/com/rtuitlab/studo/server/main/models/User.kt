package com.rtuitlab.studo.server.main.models

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,
    @SerializedName("firstname") val name: String,
    val surname: String,
    val email: String,
    val studentCardNumber: String?
)