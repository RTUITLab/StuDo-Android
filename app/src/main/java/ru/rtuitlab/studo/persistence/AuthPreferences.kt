package ru.rtuitlab.studo.persistence

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import ru.rtuitlab.studo.server.general.users.models.User

@SuppressLint("ApplySharedPref")
class AuthPreferences(context: Context) {

    companion object {
        private const val USER_KEY = "user"
        private const val ACCESS_TOKEN_KEY = "accessToken"
        private const val REFRESH_TOKEN_KEY = "refreshToken"
        const val PREFERENCES_KEY = "StuDoAuthPrefs"
    }

    private val preferences = context.getSharedPreferences(
        PREFERENCES_KEY,
        Context.MODE_PRIVATE
    )

    fun storeUser(user: User) {
        val userJSON = Gson().toJson(user)
        preferences.edit().putString(USER_KEY, userJSON).commit()
    }

    fun getUser(): User? {
        val userJSON = preferences.getString(USER_KEY, null)
        return userJSON?.let {
            Gson().fromJson(userJSON, User::class.java)
        } ?:run { null }
    }

    fun storeAccessToken(accessToken: String) {
        preferences.edit().putString(ACCESS_TOKEN_KEY, accessToken).commit()
    }

    fun getAccessToken() = preferences.getString(ACCESS_TOKEN_KEY, null)

    fun storeRefreshToken(refreshToken: String) {
        preferences.edit().putString(REFRESH_TOKEN_KEY, refreshToken).commit()
    }

    fun getRefreshToken() = preferences.getString(REFRESH_TOKEN_KEY, null)

    fun removeUserData() {
        preferences.edit().remove(USER_KEY).remove(ACCESS_TOKEN_KEY).commit()
    }
}