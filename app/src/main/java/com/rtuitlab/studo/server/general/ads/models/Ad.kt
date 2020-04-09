package com.rtuitlab.studo.server.general.ads.models

import com.rtuitlab.studo.server.general.profile.models.User

data class Ad (
    val id: String,
    val name: String,
    val shortDescription: String,
    val description: String,
    val beginTime: String,
    val endTime: String,
    val userId: String,
    val user: User,
    val organizationId: String,
    val isFavourite: Boolean
// TODO - add comments
)