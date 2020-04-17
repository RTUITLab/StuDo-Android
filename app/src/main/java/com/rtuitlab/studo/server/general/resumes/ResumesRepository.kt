package com.rtuitlab.studo.server.general.resumes

import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.ResponseHandler
import com.rtuitlab.studo.server.general.resumes.models.CompactResume
import com.rtuitlab.studo.server.general.resumes.models.CreateResumeRequest
import com.rtuitlab.studo.server.general.resumes.models.EditResumeRequest
import com.rtuitlab.studo.server.general.resumes.models.Resume
import java.lang.Exception

class ResumesRepository (
    private val resumesApi: ResumesApi,
    private val responseHandler: ResponseHandler
) {
    suspend fun getAllResumes(): Resource<List<CompactResume>> {
        return try {
            responseHandler.handleSuccess(resumesApi.getAllResumes())
        } catch (e: Exception) {
            val errorResource: Resource<List<CompactResume>> = responseHandler.handleException(e)
            if (errorResource.message == responseHandler.retryError) {
                getAllResumes()
            } else {
                errorResource
            }
        }
    }

    suspend fun getResume(resumeId: String): Resource<Resume> {
        return try {
            responseHandler.handleSuccess(resumesApi.getResume(resumeId))
        } catch (e: Exception) {
            val errorResource: Resource<Resume> = responseHandler.handleException(e)
            if (errorResource.message == responseHandler.retryError) {
                getResume(resumeId)
            } else {
                errorResource
            }
        }
    }

    suspend fun getUserResumes(userId: String): Resource<List<CompactResume>> {
        return try {
            responseHandler.handleSuccess(resumesApi.getUserResumes(userId))
        } catch (e: Exception) {
            val errorResource: Resource<List<CompactResume>> = responseHandler.handleException(e)
            if (errorResource.message == responseHandler.retryError) {
                getUserResumes(userId)
            } else {
                errorResource
            }
        }
    }



    suspend fun createResume(name: String, description: String): Resource<Resume> {
        return try {
            responseHandler.handleSuccess(resumesApi.createResume(CreateResumeRequest(
                name, description
            )))
        } catch (e: Exception) {
            val errorResource: Resource<Resume> = responseHandler.handleException(e)
            if (errorResource.message == responseHandler.retryError) {
                createResume(name, description)
            } else {
                errorResource
            }
        }
    }

    suspend fun editResume(id: String, name: String, description: String): Resource<Resume> {
        return try {
            responseHandler.handleSuccess(resumesApi.editResume(EditResumeRequest(
                id, name, description
            )))
        } catch (e: Exception) {
            val errorResource: Resource<Resume> = responseHandler.handleException(e)
            if (errorResource.message == responseHandler.retryError) {
                editResume(id, name, description)
            } else {
                errorResource
            }
        }
    }

    suspend fun deleteResume(resumeId: String): Resource<Unit> {
        return try {
            responseHandler.handleSuccess(resumesApi.deleteResume(resumeId))
        } catch (e: Exception) {
            val errorResource: Resource<Unit> = responseHandler.handleException(e)
            if (errorResource.message == responseHandler.retryError) {
                deleteResume(resumeId)
            } else {
                errorResource
            }
        }
    }
}