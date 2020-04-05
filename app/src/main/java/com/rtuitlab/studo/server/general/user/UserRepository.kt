package com.rtuitlab.studo.server.general.user

import com.rtuitlab.studo.currentUser
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.ResponseHandler
import com.rtuitlab.studo.server.general.user.models.User
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
}