package com.rtuitlab.studo.server.auth

import com.rtuitlab.studo.server.auth.models.*
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body body : UserLoginRequest): UserLoginResponse

    @POST("auth/register")
    suspend fun register(@Body body : UserRegisterRequest)

    @POST("user/password/reset")
    suspend fun resetPassword(@Body body : ResetPasswordRequest)

    @POST("auth/refresh")
    suspend fun refreshToken(@Body body : RefreshTokenRequest): UserLoginResponse
}