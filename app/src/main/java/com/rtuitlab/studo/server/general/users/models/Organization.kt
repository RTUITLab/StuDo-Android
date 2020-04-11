package com.rtuitlab.studo.server.general.users.models

data class Organization(
    val id: String,
    val name: String,
    val description: String,
    val creatorId: String,
    val creator: User
)