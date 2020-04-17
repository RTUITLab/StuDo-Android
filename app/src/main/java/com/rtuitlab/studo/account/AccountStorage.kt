package com.rtuitlab.studo.account

import android.util.Log
import com.rtuitlab.studo.persistence.EncryptedPreferences
import com.rtuitlab.studo.server.general.users.models.User
import org.koin.dsl.module

val accountStoreModule = module {
    single { AccountStorage(get()) }
}

class AccountStorage(
    private val preferences: EncryptedPreferences
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

    fun tryLoadData(): Boolean {
        preferences.getUser()?.let {
            user = it
        } ?:run {
            return false
        }
        preferences.getAccessToken()?.let {
            accessToken = it
            Log.wtf("Access Token", it)
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
}