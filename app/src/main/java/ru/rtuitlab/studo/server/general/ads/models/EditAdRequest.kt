package ru.rtuitlab.studo.server.general.ads.models

data class EditAdRequest (
    val id: String,
    val name: String,
    val description: String,
    val shortDescription: String,
    val beginTime: String,
    val endTime: String
)