package ru.rtuitlab.studo.server.auth

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import ru.rtuitlab.studo.server.auth.models.*

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body body : UserLoginRequest): UserLoginResponse

    @POST("auth/register")
    suspend fun register(@Body body : UserRegisterRequest)

    @POST("user/password/reset")
    suspend fun resetPassword(@Body body : ResetPasswordRequest)

    @POST("auth/refresh")
    fun refreshToken(@Body body : RefreshTokenRequest): Call<UserLoginResponse>
}