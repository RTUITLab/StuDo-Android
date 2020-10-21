package ru.rtuitlab.studo.server.general.users

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.rtuitlab.studo.server.general.users.models.ChangeEmailRequest
import ru.rtuitlab.studo.server.general.users.models.ChangePasswordRequest
import ru.rtuitlab.studo.server.general.users.models.ChangeUserInfoRequest
import ru.rtuitlab.studo.server.general.users.models.User

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