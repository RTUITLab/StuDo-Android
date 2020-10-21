package ru.rtuitlab.studo.server.general.ads

import retrofit2.Retrofit
import retrofit2.create

fun provideAdsApi(retrofit: Retrofit): AdsApi = retrofit.create()