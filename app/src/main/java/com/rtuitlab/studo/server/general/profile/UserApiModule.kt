package com.rtuitlab.studo.server.general.profile

import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val userApiModule = module {
    single { provideUserApi(get(named("general"))) }
}

fun provideUserApi(retrofit: Retrofit): UserApi =
    retrofit.create(UserApi::class.java)