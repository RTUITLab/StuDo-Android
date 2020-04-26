package com.rtuitlab.studo.account

import com.auth0.android.jwt.JWT
import com.rtuitlab.studo.persistence.AuthPreferences
import com.rtuitlab.studo.server.auth.models.UserLoginResponse
import com.rtuitlab.studo.server.general.users.models.User
import org.koin.dsl.module
import java.util.*

val accountStoreModule = module {
    single { AccountStorage(get()) }
}

class AccountStorage(
    private val preferences: AuthPreferences
) {
    lateinit var user: User
        private set

    lateinit var accessToken: String
        private set

    lateinit var refreshToken: String
        private set

    fun isLogged(): Boolean {
        return tryLoadData()
    }

    private fun tryLoadData(): Boolean {
        preferences.getUser()?.let {
            user = it
        } ?:run {
            return false
        }
        preferences.getAccessToken()?.let {
            accessToken = it
        } ?:run {
            return false
        }
        preferences.getRefreshToken()?.let {
            refreshToken = it
        } ?:run {
            return false
        }
        return true
    }

    fun updateUserData(newUserData: UserLoginResponse) {
        preferences.storeUser(newUserData.user)
        preferences.storeAccessToken(newUserData.accessToken)
        preferences.storeRefreshToken(newUserData.refreshToken)
        user = newUserData.user
        accessToken = newUserData.accessToken
        refreshToken = newUserData.refreshToken
    }

    fun removeUserData() = preferences.removeUserData()

    fun isAccessTokenValid() = isTokenValid(accessToken)

    fun isRefreshTokenValid() = isTokenValid(refreshToken)

    private fun isTokenValid(token: String) = JWT(token).expiresAt?.after(Date()) ?: false
}