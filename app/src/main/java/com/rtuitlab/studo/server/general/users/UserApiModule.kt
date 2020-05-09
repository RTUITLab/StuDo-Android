package com.rtuitlab.studo.server.general.users

import retrofit2.Retrofit
import retrofit2.create

fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create()