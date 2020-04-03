package com.rtuitlab.studo

import android.app.Application
import com.rtuitlab.studo.server.auth.authModule
import com.rtuitlab.studo.server.auth.authNetworkModule
import com.rtuitlab.studo.server.main.networkModule
import com.rtuitlab.studo.server.main.serverModule
import com.rtuitlab.studo.server.responseHandlerModule
import com.rtuitlab.studo.viewmodels.viewModelsModule
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
                authNetworkModule,
                authModule,
                viewModelsModule,
                networkModule,
                serverModule,
                responseHandlerModule
            ))
        }
    }
}