package com.rtuitlab.studo.server.auth

import com.rtuitlab.studo.server.auth.models.ResetPasswordRequest
import com.rtuitlab.studo.server.auth.models.UserLoginRequest
import com.rtuitlab.studo.server.auth.models.UserLoginResponse
import com.rtuitlab.studo.server.auth.models.UserRegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body body : UserLoginRequest): UserLoginResponse

    @POST("auth/register")
    suspend fun register(@Body body : UserRegisterRequest)

    @POST("user/password/reset")
    suspend fun resetPassword(@Body body : ResetPasswordRequest)
}