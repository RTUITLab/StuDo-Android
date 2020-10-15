package ru.rtuitlab.studo.di

import ru.rtuitlab.studo.account.AccountStorage
import ru.rtuitlab.studo.persistence.AuthPreferences
import ru.rtuitlab.studo.persistence.SettingsPreferences
import ru.rtuitlab.studo.recyclers.ads.AdsRecyclerAdapter
import ru.rtuitlab.studo.recyclers.resumes.ResumesRecyclerAdapter
import ru.rtuitlab.studo.server.ResponseHandler
import ru.rtuitlab.studo.server.auth.AuthRepository
import ru.rtuitlab.studo.server.auth.provideAuthApi
import ru.rtuitlab.studo.server.auth.provideAuthRetrofit
import ru.rtuitlab.studo.server.general.TokenInterceptor
import ru.rtuitlab.studo.server.general.ads.AdsRepository
import ru.rtuitlab.studo.server.general.ads.provideAdsApi
import ru.rtuitlab.studo.server.general.provideOkHttpClient
import ru.rtuitlab.studo.server.general.provideRetrofit
import ru.rtuitlab.studo.server.general.resumes.ResumesRepository
import ru.rtuitlab.studo.server.general.resumes.provideResumesApi
import ru.rtuitlab.studo.server.general.users.UserRepository
import ru.rtuitlab.studo.server.general.users.provideUserApi
import ru.rtuitlab.studo.utils.DateTimeFormatter
import ru.rtuitlab.studo.utils.provideMarkdownEditProcessor
import ru.rtuitlab.studo.utils.provideMarkdownTextProcessor
import ru.rtuitlab.studo.viewmodels.MainViewModel
import ru.rtuitlab.studo.viewmodels.ads.*
import ru.rtuitlab.studo.viewmodels.auth.*
import ru.rtuitlab.studo.viewmodels.resumes.*
import ru.rtuitlab.studo.viewmodels.users.*
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
    viewModel { MainViewModel(androidApplication(), get(), get()) }
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