package com.rtuitlab.studo.server.general

import com.rtuitlab.studo.account.AccountStorage
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val accStorage: AccountStorage
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer ${accStorage.accessToken}")
            .build()
        return chain.proceed(request)
    }
}