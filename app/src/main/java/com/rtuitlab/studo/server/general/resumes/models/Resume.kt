package com.rtuitlab.studo.server.general.resumes.models

import com.rtuitlab.studo.server.general.users.models.User

data class Resume(
    val id: String,
    val name: String,
    val description: String,
    val userId: String,
    val user: User
)