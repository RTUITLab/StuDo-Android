package com.rtuitlab.studo.server.general.resumes.models

data class EditResumeRequest(
    val id: String,
    val name: String,
    val description: String
)