package ru.rtuitlab.studo.server.auth

import ru.rtuitlab.studo.server.Resource
import ru.rtuitlab.studo.server.ResponseHandler
import ru.rtuitlab.studo.server.auth.models.ResetPasswordRequest
import ru.rtuitlab.studo.server.auth.models.UserLoginRequest
import ru.rtuitlab.studo.server.auth.models.UserLoginResponse
import ru.rtuitlab.studo.server.auth.models.UserRegisterRequest

class AuthRepository(
    private val authApi: AuthApi,
    private val responseHandler: ResponseHandler
) {
    suspend fun login(email: String, password: String): Resource<UserLoginResponse> {
        return try {
            responseHandler.handleSuccess(authApi.login(UserLoginRequest(email, password)))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun register(
        name: String,
        surname: String,
        email: String,
        cardNumber: String,
        password: String,
        passwordConfirm: String
    ): Resource<Unit> {
        return try {
            responseHandler.handleSuccess(authApi.register(UserRegisterRequest(
                name, surname, email, cardNumber, password, passwordConfirm
            )))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun resetPassword(email: String): Resource<Unit> {
        return try {
            responseHandler.handleSuccess(authApi.resetPassword(ResetPasswordRequest(email)))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}