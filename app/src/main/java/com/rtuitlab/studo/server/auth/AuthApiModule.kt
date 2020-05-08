package com.rtuitlab.studo.server.auth

import com.rtuitlab.studo.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

fun provideAuthRetrofit(): Retrofit =
    Retrofit.Builder().baseUrl(BuildConfig.API_URL).addConverterFactory(GsonConverterFactory.create()).build()

fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create()