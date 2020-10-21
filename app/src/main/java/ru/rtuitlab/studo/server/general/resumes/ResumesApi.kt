package ru.rtuitlab.studo.server.general.resumes

import retrofit2.http.*
import ru.rtuitlab.studo.server.general.resumes.models.CompactResume
import ru.rtuitlab.studo.server.general.resumes.models.CreateResumeRequest
import ru.rtuitlab.studo.server.general.resumes.models.EditResumeRequest
import ru.rtuitlab.studo.server.general.resumes.models.Resume

interface ResumesApi {
    @GET("resumes")
    suspend fun getAllResumes() : List<CompactResume>

    @GET("resumes/{resumeId}")
    suspend fun getResume(@Path("resumeId") resumeId: String): Resume

    @GET("resumes/user/{userId}")
    suspend fun getUserResumes(@Path("userId") userId: String): List<CompactResume>



    @POST("resumes")
    suspend fun createResume(@Body body: CreateResumeRequest): Resume

    @PUT("resumes")
    suspend fun editResume(@Body body: EditResumeRequest): Resume

    @DELETE("resumes/{resumeId}")
    suspend fun deleteResume(@Path("resumeId") resumeId : String)
}