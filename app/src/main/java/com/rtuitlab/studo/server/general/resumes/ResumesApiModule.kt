package com.rtuitlab.studo.server.general.resumes

import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val resumesApiModule = module {
    single { provideResumesApi(get(named("general"))) }
}

fun provideResumesApi(retrofit: Retrofit): ResumesApi =
    retrofit.create(ResumesApi::class.java)