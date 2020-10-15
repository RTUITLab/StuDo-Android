package ru.rtuitlab.studo.server.general.resumes.models

import ru.rtuitlab.studo.server.general.users.models.User
import java.io.Serializable

data class Resume(
    val id: String,
    val name: String,
    val description: String,
    val userId: String,
    val user: User
): Serializable