package com.rtuitlab.studo.account

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
        return true
    }
}