package com.rtuitlab.studo.server.general.ads

import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val adsApiModule = module {
    single { provideAdsApi(get(named("general"))) }
}

fun provideAdsApi(retrofit: Retrofit): AdsApi =
    retrofit.create(AdsApi::class.java)