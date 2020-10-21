package ru.rtuitlab.studo.server.general.resumes.models

import java.io.Serializable

data class CompactResume(
    val id : String,
    val name : String,
    val description : String,
    val userName : String
): Serializable