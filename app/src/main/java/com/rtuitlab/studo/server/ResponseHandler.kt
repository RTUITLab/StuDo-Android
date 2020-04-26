package com.rtuitlab.studo.server

import android.util.Log
import org.koin.dsl.module
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

val responseHandlerModule = module {
    factory { ResponseHandler() }
}

class ResponseHandler {
    fun <T : Any> handleSuccess(data: T): Resource<T> {
        return Resource.success(data)
    }

    fun <T : Any> handleException(e: Exception): Resource<T> {
        Log.e(javaClass.name, e.toString())
        return when (e) {
            is HttpException -> Resource.error(getErrorMessage(e.code()), null)
            is SocketTimeoutException -> Resource.error(getErrorMessage(ErrorCodes.SocketTimeOut.code), null)
            is UnknownHostException -> Resource.error(getErrorMessage(ErrorCodes.UnknownHost.code), null)
            else -> Resource.error(getErrorMessage(Int.MAX_VALUE), null)
        }
    }

    private fun getErrorMessage(code: Int): String {
        return when (code) {
            ErrorCodes.UnknownHost.code -> "Check your internet connection"
            ErrorCodes.SocketTimeOut.code -> "Timeout"
            401 -> "Unauthorized"
            404 -> "Not found"
            in 400..499 -> "Check entered data"
            in 500..599 -> "Error with connecting to server. Code: $code"
            else -> "Something went wrong"
        }
    }

    private enum class ErrorCodes(val code: Int) {
        SocketTimeOut(-1),
        UnknownHost(-2)
    }
}