package com.rtuitlab.studo.server.main

import com.rtuitlab.studo.BuildConfig
import com.rtuitlab.studo.server.ResponseHandler
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    factory { TokenInterceptor() }
    factory { provideOkHttpClient(get()) }
    single { provideServerApi(get(named("server"))) }
    single(named("server")) { provideRetrofit(get()) }
}

fun provideOkHttpClient(interceptor: TokenInterceptor): OkHttpClient =
    OkHttpClient().newBuilder().addInterceptor(interceptor).build()

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
    Retrofit.Builder().baseUrl(BuildConfig.API_URL).client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create()).build()

fun provideServerApi(retrofit: Retrofit): ServerApi =
    retrofit.create(ServerApi::class.java)