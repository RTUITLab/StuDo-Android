package com.rtuitlab.studo.server.general.resumes

import com.rtuitlab.studo.server.general.resumes.models.CompactResume
import retrofit2.http.GET

interface ResumesApi {
    @GET("resumes")
    suspend fun getAllResumes() : List<CompactResume>
}