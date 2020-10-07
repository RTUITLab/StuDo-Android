package com.rtuitlab.studo.di

import com.rtuitlab.studo.account.AccountStorage
import com.rtuitlab.studo.persistence.AuthPreferences
import com.rtuitlab.studo.persistence.SettingsPreferences
import com.rtuitlab.studo.recyclers.ads.AdsRecyclerAdapter
import com.rtuitlab.studo.recyclers.resumes.ResumesRecyclerAdapter
import com.rtuitlab.studo.server.ResponseHandler
import com.rtuitlab.studo.server.auth.AuthRepository
import com.rtuitlab.studo.server.auth.provideAuthApi
import com.rtuitlab.studo.server.auth.provideAuthRetrofit
import com.rtuitlab.studo.server.general.TokenInterceptor
import com.rtuitlab.studo.server.general.ads.AdsRepository
import com.rtuitlab.studo.server.general.ads.provideAdsApi
import com.rtuitlab.studo.server.general.provideOkHttpClient
import com.rtuitlab.studo.server.general.provideRetrofit
import com.rtuitlab.studo.server.general.resumes.ResumesRepository
import com.rtuitlab.studo.server.general.resumes.provideResumesApi
import com.rtuitlab.studo.server.general.users.UserRepository
import com.rtuitlab.studo.server.general.users.provideUserApi
import com.rtuitlab.studo.utils.DateTimeFormatter
import com.rtuitlab.studo.utils.provideMarkdownEditProcessor
import com.rtuitlab.studo.utils.provideMarkdownTextProcessor
import com.rtuitlab.studo.viewmodels.MainViewModel
import com.rtuitlab.studo.viewmodels.ads.*
import com.rtuitlab.studo.viewmodels.auth.*
import com.rtuitlab.studo.viewmodels.resumes.*
import com.rtuitlab.studo.viewmodels.users.*
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val retrofitModule = module {
    factory { TokenInterceptor(androidApplication(), get(), get()) }
    factory { provideOkHttpClient(get()) }
    single { provideAdsApi(get(named("general"))) }
    single { provideResumesApi(get(named("general"))) }
    single { provideUserApi(get(named("general"))) }
    single { provideAuthApi(get(named("auth"))) }
    single(named("general")) { provideRetrofit(get()) }
    single(named("auth")) { provideAuthRetrofit() }
}

val responseHandlerModule = module {
    factory { ResponseHandler() }
}

val viewModelsModule = module {
    viewModel { AuthViewModel(androidApplication(), get(), get()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { AdsListViewModel(get(), get()) }
    viewModel { AdViewModel(androidApplication(), get(), get(), get(), get((named("text")))) }
    viewModel { CreateEditAdViewModel(get(), get()) }
    viewModel { CommentsViewModel(get()) }
    viewModel { ResumesListViewModel(get(), get()) }
    viewModel { ResumeViewModel(get(), get()) }
    viewModel { CreateEditResumeViewModel(get()) }
    viewModel { ProfileViewModel(androidApplication(), get(), get(), get()) }
    viewModel { AccountChangesDialogsViewModel(get()) }
    viewModel { OtherUserViewModel(get(), get()) }
}

val repositoriesModule = module {
    single { AuthRepository(get(), get()) }
    single { AdsRepository(get(), get()) }
    single { ResumesRepository(get(), get()) }
    single { UserRepository(get(), get(), get()) }
}

val persistenceModule = module {
    single { AuthPreferences(androidContext()) }
    single { SettingsPreferences(androidContext()) }
}

val adaptersModule = module {
    factory { AdsRecyclerAdapter() }
    factory { ResumesRecyclerAdapter() }
}

val accountStoreModule = module {
    single { AccountStorage(get()) }
}

val timeFormatterModule = module {
    single { DateTimeFormatter() }
}

val markdownModule = module {
    single(named("text")) { provideMarkdownTextProcessor(androidApplication()) }
    single(named("edit")) { provideMarkdownEditProcessor(androidApplication()) }
}