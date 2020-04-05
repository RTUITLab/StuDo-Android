package com.rtuitlab.studo.server.auth

import com.rtuitlab.studo.BuildConfig
import com.rtuitlab.studo.server.ResponseHandler
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val authApiModule = module {
    single(named("auth")) { provideAuthRetrofit() }
    single { provideAuthApi(get(named("auth"))) }
}

fun provideAuthRetrofit(): Retrofit =
    Retrofit.Builder().baseUrl(BuildConfig.API_URL).addConverterFactory(GsonConverterFactory.create()).build()

fun provideAuthApi(retrofit: Retrofit): AuthApi =
    retrofit.create(AuthApi::class.java)