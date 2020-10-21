package ru.rtuitlab.studo.server.general.ads.models

data class CreateAdRequest (
    val name: String,
    val description: String,
    val shortDescription: String,
    val beginTime: String,
    val endTime: String,
    val organizationId: String? = null
)