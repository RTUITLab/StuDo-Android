package com.rtuitlab.studo.server.general.users.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    val id: String,
    @SerializedName("firstname") val name: String,
    val surname: String,
    val email: String,
    val studentCardNumber: String?
) : Serializable