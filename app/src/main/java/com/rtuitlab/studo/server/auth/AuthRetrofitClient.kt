package com.rtuitlab.studo.server.auth

import com.rtuitlab.studo.BuildConfig
import com.rtuitlab.studo.server.ResponseHandler
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val authNetworkModule = module {
//    factory { provideOkHttpClient() }
    single { provideAuthRetrofit() }
    factory { provideAuthApi(get()) }
    factory { ResponseHandler() }
}

//fun provideOkHttpClient(): OkHttpClient = OkHttpClient().newBuilder().build()

fun provideAuthRetrofit(/*okHttpClient: OkHttpClient*/): Retrofit =
    Retrofit.Builder().baseUrl(BuildConfig.API_URL)/*.client(okHttpClient)*/
        .addConverterFactory(GsonConverterFactory.create()).build()

fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)