package com.rtuitlab.studo

import android.app.Application
import com.rtuitlab.studo.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(
                retrofitModule,
                responseHandlerModule,
                viewModelsModule,
                repositoriesModule,
                persistenceModule,
                adaptersModule,
                accountStoreModule,
                timeFormatterModule,
                markdownModule
            ))
        }
    }
}