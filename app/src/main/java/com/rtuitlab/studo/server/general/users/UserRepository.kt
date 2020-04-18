package com.rtuitlab.studo.server.general.users

import com.rtuitlab.studo.account.AccountStorage
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.ResponseHandler
import com.rtuitlab.studo.server.general.users.models.ChangeEmailRequest
import com.rtuitlab.studo.server.general.users.models.ChangePasswordRequest
import com.rtuitlab.studo.server.general.users.models.ChangeUserInfoRequest
import com.rtuitlab.studo.server.general.users.models.User
import java.lang.Exception

class UserRepository (
    private val userApi: UserApi,
    private val responseHandler: ResponseHandler,
    private val accStorage: AccountStorage
) {
    suspend fun loadCurrentUser(): Resource<User> {
        return try {
            responseHandler.handleSuccess(userApi.getUser(accStorage.user.id))
        } catch (e: Exception) {
            val errorResource: Resource<User> = responseHandler.handleException(e)
            if (errorResource.message == responseHandler.retryError) {
                loadCurrentUser()
            } else {
                errorResource
            }
        }
    }

    suspend fun loadUser(userId: String): Resource<User> {
        return try {
            responseHandler.handleSuccess(userApi.getUser(userId))
        } catch (e: Exception) {
            val errorResource: Resource<User> = responseHandler.handleException(e)
            if (errorResource.message == responseHandler.retryError) {
                loadCurrentUser()
            } else {
                errorResource
            }
        }
    }

    suspend fun changeUserInfo(name: String, surname: String, cardNumber: String): Resource<User> {
        return try {
            responseHandler.handleSuccess(userApi.changeUserInfo(
                ChangeUserInfoRequest(
                    accStorage.user.id, name, surname, cardNumber
            )))
        } catch (e: Exception) {
            val errorResource: Resource<User> = responseHandler.handleException(e)
            if (errorResource.message == responseHandler.retryError) {
                changeUserInfo(name, surname, cardNumber)
            } else {
                errorResource
            }
        }
    }

    suspend fun changeEmail(oldEmail: String, newEmail: String): Resource<Unit> {
        return try {
            responseHandler.handleSuccess(userApi.changeEmail(ChangeEmailRequest(
                accStorage.user.id, oldEmail, newEmail
            )))
        } catch (e: Exception) {
            val errorResource: Resource<Unit> = responseHandler.handleException(e)
            if (errorResource.message == responseHandler.retryError) {
                changeEmail(oldEmail, newEmail)
            } else {
                errorResource
            }
        }
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): Resource<Unit> {
        return try {
            responseHandler.handleSuccess(userApi.changePassword(ChangePasswordRequest(
                accStorage.user.id, oldPassword, newPassword
            )))
        } catch (e: Exception) {
            val errorResource: Resource<Unit> = responseHandler.handleException(e)
            if (errorResource.message == responseHandler.retryError) {
                changePassword(oldPassword, newPassword)
            } else {
                errorResource
            }
        }
    }
}