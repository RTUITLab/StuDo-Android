package com.rtuitlab.studo.server.general.profile

import com.rtuitlab.studo.currentUser
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.ResponseHandler
import com.rtuitlab.studo.server.general.profile.models.ChangeEmailRequest
import com.rtuitlab.studo.server.general.profile.models.ChangePasswordRequest
import com.rtuitlab.studo.server.general.profile.models.ChangeUserInfoRequest
import com.rtuitlab.studo.server.general.profile.models.User
import java.lang.Exception

class UserRepository (
    private val userApi: UserApi,
    private val responseHandler: ResponseHandler
) {
    suspend fun loadCurrentUser(): Resource<User> {
        return try {
            responseHandler.handleSuccess(userApi.getUser(currentUser!!.id))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun changeUserInfo(name: String, surname: String, cardNumber: String): Resource<User> {
        return try {
            responseHandler.handleSuccess(userApi.changeUserInfo(
                ChangeUserInfoRequest(
                    currentUser!!.id, name, surname, cardNumber
            )))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun changeEmail(oldEmail: String, newEmail: String): Resource<Unit> {
        return try {
            responseHandler.handleSuccess(userApi.changeEmail(ChangeEmailRequest(
                currentUser!!.id, oldEmail, newEmail
            )))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): Resource<Unit> {
        return try {
            responseHandler.handleSuccess(userApi.changePassword(ChangePasswordRequest(
                currentUser!!.id, oldPassword, newPassword
            )))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}