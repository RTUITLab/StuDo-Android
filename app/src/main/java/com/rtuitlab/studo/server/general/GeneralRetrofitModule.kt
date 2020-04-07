package com.rtuitlab.studo.server.general

import com.rtuitlab.studo.BuildConfig
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val generalRetrofitModule = module {
    factory { TokenInterceptor(get()) }
    factory { provideOkHttpClient(get()) }
    single(named("general")) { provideRetrofit(get()) }
}

fun provideOkHttpClient(interceptor: TokenInterceptor): OkHttpClient =
    OkHttpClient().newBuilder().addInterceptor(interceptor).build()

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
    Retrofit.Builder().baseUrl(BuildConfig.API_URL).client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create()).build()