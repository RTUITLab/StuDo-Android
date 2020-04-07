package com.rtuitlab.studo.server.general

import com.rtuitlab.studo.account.AccountStore
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val accStore: AccountStore
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer ${accStore.accessToken}")
            .build()
        return chain.proceed(request)
    }
}