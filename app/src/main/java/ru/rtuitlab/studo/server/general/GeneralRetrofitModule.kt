package ru.rtuitlab.studo.server.general

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.rtuitlab.studo.BuildConfig

fun provideOkHttpClient(interceptor: TokenInterceptor): OkHttpClient =
    OkHttpClient().newBuilder().addInterceptor(interceptor).build()

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
    Retrofit.Builder().baseUrl(BuildConfig.API_URL).client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create()).build()