package ru.rtuitlab.studo.server.general.resumes

import ru.rtuitlab.studo.server.Resource
import ru.rtuitlab.studo.server.ResponseHandler
import ru.rtuitlab.studo.server.general.resumes.models.CompactResume
import ru.rtuitlab.studo.server.general.resumes.models.CreateResumeRequest
import ru.rtuitlab.studo.server.general.resumes.models.EditResumeRequest
import ru.rtuitlab.studo.server.general.resumes.models.Resume

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

    suspend fun getResume(resumeId: String): Resource<Resume> {
        return try {
            responseHandler.handleSuccess(resumesApi.getResume(resumeId))
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



    suspend fun createResume(name: String, description: String): Resource<Resume> {
        return try {
            responseHandler.handleSuccess(resumesApi.createResume(CreateResumeRequest(
                name, description
            )))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun editResume(id: String, name: String, description: String): Resource<Resume> {
        return try {
            responseHandler.handleSuccess(resumesApi.editResume(EditResumeRequest(
                id, name, description
            )))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun deleteResume(resumeId: String): Resource<Unit> {
        return try {
            responseHandler.handleSuccess(resumesApi.deleteResume(resumeId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}