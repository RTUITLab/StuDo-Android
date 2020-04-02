package com.rtuitlab.studo.server.auth

import com.rtuitlab.studo.server.ResponseHandler
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.auth.models.ResetPasswordRequest
import com.rtuitlab.studo.server.auth.models.UserLoginRequest
import com.rtuitlab.studo.server.auth.models.UserLoginResponse
import com.rtuitlab.studo.server.auth.models.UserRegisterRequest
import org.koin.dsl.module
import java.lang.Exception

val authModule = module {
    single { AuthRepository(get(), get()) }
}

class AuthRepository(
    private val authApi: AuthApi,
    private val responseHandler: ResponseHandler
) {
    suspend fun login(userLoginRequest: UserLoginRequest): Resource<UserLoginResponse> {
        return try {
            responseHandler.handleSuccess(authApi.login(userLoginRequest))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun register(userRegisterRequest: UserRegisterRequest): Resource<Unit> {
        return try {
            responseHandler.handleSuccess(authApi.register(userRegisterRequest))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): Resource<Unit> {
        return try {
            responseHandler.handleSuccess(authApi.resetPassword(resetPasswordRequest))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}