package com.rtuitlab.studo.server.general.user

import com.rtuitlab.studo.server.general.user.models.ChangeUserInfoRequest
import com.rtuitlab.studo.server.general.user.models.User
import retrofit2.http.*

interface UserApi {
    @GET("user/{userId}")
    suspend fun getUser(@Path("userId") userId : String): User

    @POST("user/change/info")
    suspend fun changeUserInfo(@Body body : ChangeUserInfoRequest) : User
}