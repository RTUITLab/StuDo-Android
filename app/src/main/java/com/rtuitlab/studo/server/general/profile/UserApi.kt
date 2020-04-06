package com.rtuitlab.studo.server.general.profile

import com.rtuitlab.studo.server.general.profile.models.ChangeEmailRequest
import com.rtuitlab.studo.server.general.profile.models.ChangePasswordRequest
import com.rtuitlab.studo.server.general.profile.models.ChangeUserInfoRequest
import com.rtuitlab.studo.server.general.profile.models.User
import retrofit2.http.*

interface UserApi {
    @GET("user/{userId}")
    suspend fun getUser(@Path("userId") userId : String): User

    @POST("user/change/info")
    suspend fun changeUserInfo(@Body body : ChangeUserInfoRequest) : User

    @POST("user/change/email")
    suspend fun changeEmail(@Body body : ChangeEmailRequest)

    @POST("user/password/change")
    suspend fun changePassword(@Body body : ChangePasswordRequest)
}