package com.rtuitlab.studo.server.general.resumes

import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.ResponseHandler
import com.rtuitlab.studo.server.general.resumes.models.CompactResume
import java.lang.Exception

class ResumesRepository (
    private val resumesApi: ResumesApi,
    private val responseHandler: ResponseHandler
) {
    suspend fun getAllResumes(): Resource<List<CompactResume>> {
        return try {
            responseHandler.handleSuccess(resumesApi.getAllResumes())
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun getUserResumes(userId: String): Resource<List<CompactResume>> {
        return try {
            responseHandler.handleSuccess(resumesApi.getUserResumes(userId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}