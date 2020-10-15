package ru.rtuitlab.studo

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.rtuitlab.studo.di.*
import ru.rtuitlab.studo.utils.RuntimeLocaleChanger

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
            // TODO Await fix for Koin and replace the explicit invocations
            //  of loadModules() and createRootScope() with a single call to modules()
            //  (https://github.com/InsertKoinIO/koin/issues/847)
            koin.loadModules(listOf(
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
            koin.createRootScope()
        }
    }
}