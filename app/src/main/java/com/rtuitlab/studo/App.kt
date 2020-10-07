package com.rtuitlab.studo

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.rtuitlab.studo.di.*
import com.rtuitlab.studo.utils.RuntimeLocaleChanger
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(RuntimeLocaleChanger.wrapContext(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        RuntimeLocaleChanger.overrideLocale(this)
    }

    private fun initKoin() {
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