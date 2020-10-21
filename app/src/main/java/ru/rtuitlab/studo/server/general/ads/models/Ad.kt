package ru.rtuitlab.studo.server.general.ads.models

import com.google.gson.annotations.SerializedName
import ru.rtuitlab.studo.server.general.users.models.Organization
import ru.rtuitlab.studo.server.general.users.models.User
import java.io.Serializable

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
    @SerializedName("isFavorite") val isFavourite: Boolean,
    var comments: List<Comment>
) : Serializable