package com.rtuitlab.studo.server

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.rtuitlab.studo.account.AccountStorage
import com.rtuitlab.studo.persistence.EncryptedPreferences
import com.rtuitlab.studo.server.auth.AuthApi
import com.rtuitlab.studo.server.auth.models.RefreshTokenRequest
import com.rtuitlab.studo.ui.general.MainActivity
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.system.exitProcess

val responseHandlerModule = module {
    factory { ResponseHandler(androidApplication(), get(), get(), get()) }
}

class ResponseHandler(
    private val context: Application,
    private val authApi: AuthApi,
    private val encryptedPrefs: EncryptedPreferences,
    private val accStorage: AccountStorage
) {

    val retryError = "Retry"

    fun <T : Any> handleSuccess(data: T): Resource<T> {
        return Resource.success(data)
    }

    suspend fun <T : Any> handleException(e: Exception): Resource<T> {
        Log.e(javaClass.name, e.toString())
        return when (e) {
            is HttpException -> {
                if (e.code() == 401) {
                    try {
                        tryUpdateToken()
                        Resource.error(getErrorMessage(ErrorCodes.Retry.code), null)
                    } catch (exception: java.lang.Exception) {
                        if (exception is HttpException && exception.code() in 400..404) {
                            logoutAndRestart()
                        }
                        Resource.error(getErrorMessage(e.code()), null)
                    }
                } else {
                    Resource.error(getErrorMessage(e.code()), null)
                }
            }
            is SocketTimeoutException -> Resource.error(getErrorMessage(ErrorCodes.SocketTimeOut.code), null)
            is UnknownHostException -> Resource.error(getErrorMessage(ErrorCodes.UnknownHost.code), null)
            else -> Resource.error(getErrorMessage(Int.MAX_VALUE), null)
        }
    }

    private fun getErrorMessage(code: Int): String {
        return when (code) {
            ErrorCodes.Retry.code -> "Retry"
            ErrorCodes.UnknownHost.code -> "Check your internet connection"
            ErrorCodes.SocketTimeOut.code -> "Timeout"
            401 -> "Unauthorized"
            404 -> "Not found"
            in 400..499 -> "Check entered data"
            in 500..599 -> "Error with connecting to server. Code: $code"
            else -> "Something went wrong"
        }
    }

    private suspend fun tryUpdateToken() {
        Log.wtf(javaClass.name, "Update refresh token")
        val response = authApi.refreshToken(RefreshTokenRequest(accStorage.refreshToken))
        encryptedPrefs.storeUser(response.user)
        encryptedPrefs.storeAccessToken(response.accessToken)
        encryptedPrefs.storeRefreshToken(response.refreshToken)
        accStorage.tryLoadData()
    }

    private fun logoutAndRestart() {
        Log.wtf(javaClass.name, "Logout")
        encryptedPrefs.removeUserData()

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            ErrorCodes.Retry.code,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val mgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10, pendingIntent)
        exitProcess(0)
    }

    private enum class ErrorCodes(val code: Int) {
        SocketTimeOut(-1),
        UnknownHost(-2),
        Retry(-3)
    }
}