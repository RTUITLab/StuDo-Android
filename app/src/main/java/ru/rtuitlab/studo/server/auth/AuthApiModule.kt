package ru.rtuitlab.studo.server.auth

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.rtuitlab.studo.BuildConfig

fun provideAuthRetrofit(): Retrofit =
    Retrofit.Builder().baseUrl(BuildConfig.API_URL).addConverterFactory(GsonConverterFactory.create()).build()

fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create()