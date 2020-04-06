package com.rtuitlab.studo.viewmodels

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { AuthViewModel(androidApplication(), get()) }
    viewModel { AdsViewModel(get()) }
    viewModel { ResumesViewModel(get()) }
    viewModel { ProfileViewModel(androidApplication(), get()) }
}