package com.rtuitlab.studo.server.general.ads.models

data class Comment(
    val id: String,
    val text: String,
    val commentTime: String,
    val authorId: String,
    val author: String
)