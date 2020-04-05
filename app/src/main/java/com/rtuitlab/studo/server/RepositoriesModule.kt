package com.rtuitlab.studo.server

import com.rtuitlab.studo.server.auth.AuthRepository
import com.rtuitlab.studo.server.general.ads.AdsRepository
import com.rtuitlab.studo.server.general.resumes.ResumesRepository
import com.rtuitlab.studo.server.general.user.UserRepository
import org.koin.dsl.module

val repositoriesModule = module {
    single { AuthRepository(get(), get()) }
    single { AdsRepository(get(), get()) }
    single { ResumesRepository(get(), get()) }
    single { UserRepository(get(), get()) }
}