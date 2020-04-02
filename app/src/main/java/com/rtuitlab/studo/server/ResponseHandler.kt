package com.rtuitlab.studo.server

import android.util.Log
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

enum class ErrorCodes(val code: Int) {
    SocketTimeOut(-1),
    UnknownHost(-2)
}

open class ResponseHandler {
    fun <T : Any> handleSuccess(data: T): Resource<T> {
        return Resource.success(data)
    }

    fun <T : Any> handleException(e: Exception): Resource<T> {
        Log.wtf("hey", "Ex: $e")
        return when (e) {
            is HttpException -> Resource.error(getErrorMessage(e.code()), null)
            is SocketTimeoutException -> Resource.error(getErrorMessage(ErrorCodes.SocketTimeOut.code), null)
            is UnknownHostException -> Resource.error(getErrorMessage(ErrorCodes.UnknownHost.code), null)
            else -> Resource.error(getErrorMessage(Int.MAX_VALUE), null)
        }
    }

    private fun getErrorMessage(code: Int): String {
        Log.wtf("hey", code.toString())
        return when (code) {
            ErrorCodes.UnknownHost.code -> "Check your internet connection"
            ErrorCodes.SocketTimeOut.code -> "Timeout"
            401 -> "Unauthorised"
            404 -> "Not found"
            else -> "Something went wrong"
        }
    }
}