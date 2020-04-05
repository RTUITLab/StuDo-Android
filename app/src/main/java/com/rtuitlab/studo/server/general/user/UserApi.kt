package com.rtuitlab.studo.server.general.user

import com.rtuitlab.studo.server.general.user.models.User
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {
    @GET("user/{userId}")
    suspend fun getUser(@Path("userId") userId : String): User
}