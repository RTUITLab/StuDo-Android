package ru.rtuitlab.studo.server.general.ads.models

import java.io.Serializable

data class Comment(
    val id: String,
    val text: String,
    val commentTime: String,
    val authorId: String,
    val author: String
): Serializable