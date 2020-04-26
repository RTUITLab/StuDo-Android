package com.rtuitlab.studo.server.general.users.models

import java.io.Serializable

data class Organization(
    val id: String,
    val name: String,
    val description: String,
    val creatorId: String,
    val creator: User
): Serializable