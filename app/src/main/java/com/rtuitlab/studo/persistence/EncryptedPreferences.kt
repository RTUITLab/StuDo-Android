package com.rtuitlab.studo.persistence

import android.annotation.SuppressLint
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import com.rtuitlab.studo.server.general.users.models.User
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val encryptedPrefModule = module {
    single { EncryptedPreferences(androidContext()) }
}

@SuppressLint("ApplySharedPref")
class EncryptedPreferences(context: Context) {

    private val userKey = "user"
    private val accessTokenKey = "accessToken"
    private val refreshTokenKey = "refreshToken"

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val preferences = EncryptedSharedPreferences.create(
        "StuDoEncryptedPrefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun storeUser(user: User) {
        val userJSON = Gson().toJson(user)
        preferences.edit().putString(userKey, userJSON).commit()
    }

    fun getUser(): User? {
        val userJSON = preferences.getString(userKey, null)
        return userJSON?.let {
            Gson().fromJson(userJSON, User::class.java)
        } ?:run { null }
    }

    fun storeAccessToken(accessToken: String) {
        preferences.edit().putString(accessTokenKey, accessToken).commit()
    }

    fun getAccessToken(): String? {
        return preferences.getString(accessTokenKey, null)
    }

    fun storeRefreshToken(refreshToken: String) {
        preferences.edit().putString(refreshTokenKey, refreshToken).commit()
    }

    fun getRefreshToken(): String? {
        return preferences.getString(refreshTokenKey, null)
    }

    fun removeUserData() {
        preferences.edit().remove(userKey).remove(accessTokenKey).commit()
    }
}