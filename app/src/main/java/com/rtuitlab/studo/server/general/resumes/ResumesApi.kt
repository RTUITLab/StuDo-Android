package com.rtuitlab.studo.server.general.resumes

import com.rtuitlab.studo.server.general.resumes.models.CompactResume
import retrofit2.http.GET
import retrofit2.http.Path

interface ResumesApi {
    @GET("resumes")
    suspend fun getAllResumes() : List<CompactResume>

    @GET("resumes/user/{userId}")
    suspend fun getUserResumes(@Path("userId") userId: String): List<CompactResume>
}