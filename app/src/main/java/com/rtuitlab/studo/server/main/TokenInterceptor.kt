package com.rtuitlab.studo.server.main

import com.rtuitlab.studo.accessToken
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer ${accessToken!!}") // TODO - Remove "!!"
            .build()
        return chain.proceed(request)
    }
}