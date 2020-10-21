package ru.rtuitlab.studo.server.general.resumes

import retrofit2.Retrofit
import retrofit2.create

fun provideResumesApi(retrofit: Retrofit): ResumesApi = retrofit.create()