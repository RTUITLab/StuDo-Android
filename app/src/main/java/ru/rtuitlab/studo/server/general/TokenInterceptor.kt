package ru.rtuitlab.studo.server.general

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import ru.rtuitlab.studo.account.AccountStorage
import ru.rtuitlab.studo.server.auth.AuthApi
import ru.rtuitlab.studo.server.auth.models.RefreshTokenRequest
import ru.rtuitlab.studo.ui.general.MainActivity
import kotlin.system.exitProcess

class TokenInterceptor(
    private val app: Application,
    private val authApi: AuthApi,
    private val accStorage: AccountStorage
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer ${getAccessToken()}")
            .build()
        return chain.proceed(request)
    }

    @Synchronized
    private fun getAccessToken(): String {
        val isAccessTokenValid = accStorage.isAccessTokenValid()
        val isRefreshTokenValid = accStorage.isRefreshTokenValid()

        if (!isAccessTokenValid && isRefreshTokenValid) {
            try {
                Log.i("Interceptor", "Refresh tokens")
                val newUserData =
                    authApi.refreshToken(RefreshTokenRequest(accStorage.refreshToken)).execute()
                accStorage.updateUserData(newUserData.body()!!)
            } catch (e: Exception) { }
        } else if (!isRefreshTokenValid){
            logoutAndRestart()
        }

        return accStorage.accessToken
    }

    private fun logoutAndRestart() {
        accStorage.removeUserData()

        val intent = Intent(app, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            app,
            0,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val mgr = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10, pendingIntent)
        exitProcess(0)
    }
}