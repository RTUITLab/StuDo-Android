package ru.rtuitlab.studo.server.general.ads.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CompactAd(
    val id : String,
    val name : String,
    val shortDescription : String,
    val beginTime : String,
    val endTime : String,
    val userId: String?,
    val userName : String?,
    val organizationId: String?,
    val organizationName : String?,
    @SerializedName("isFavorite") var isFavourite: Boolean
): Serializable