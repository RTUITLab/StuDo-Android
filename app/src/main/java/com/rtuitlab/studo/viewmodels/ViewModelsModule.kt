package com.rtuitlab.studo.viewmodels

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { AuthViewModel(androidApplication(), get(), get()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { AdsListViewModel(get(), get()) }
    viewModel { ResumesListViewModel(get(), get()) }
    viewModel { ProfileViewModel(androidApplication(), get(), get(), get()) }
    viewModel { AccountChangesDialogsViewModel(get()) }
    viewModel { AdViewModel(androidApplication(), get(), get(), get()) }
    viewModel { CreateEditAdViewModel(androidApplication(), get(), get()) }
}