package com.rtuitlab.studo.server.general.ads.models

import com.google.gson.annotations.SerializedName
import com.rtuitlab.studo.server.general.users.models.Organization
import com.rtuitlab.studo.server.general.users.models.User

data class Ad (
    val id: String,
    val name: String,
    val shortDescription: String,
    var description: String,
    val beginTime: String,
    val endTime: String,
    val userId: String?,
    val user: User?,
    val organizationId: String?,
    val organization: Organization?,
    @SerializedName("isFavorite") var isFavourite: Boolean,
    val comments: List<Comment>
)