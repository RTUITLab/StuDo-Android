package com.rtuitlab.studo

import android.app.Application
import com.rtuitlab.studo.account.accountStoreModule
import com.rtuitlab.studo.persistence.encryptedPrefModule
import com.rtuitlab.studo.persistence.settingsPrefModule
import com.rtuitlab.studo.server.auth.authApiModule
import com.rtuitlab.studo.server.general.ads.adsApiModule
import com.rtuitlab.studo.server.general.generalRetrofitModule
import com.rtuitlab.studo.server.general.resumes.resumesApiModule
import com.rtuitlab.studo.server.general.users.userApiModule
import com.rtuitlab.studo.server.repositoriesModule
import com.rtuitlab.studo.server.responseHandlerModule
import com.rtuitlab.studo.utils.markdownModule
import com.rtuitlab.studo.utils.timeFormatterModule
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
                authApiModule,
                generalRetrofitModule,
                adsApiModule,
                resumesApiModule,
                userApiModule,
                responseHandlerModule,
                repositoriesModule,
                encryptedPrefModule,
                settingsPrefModule,
                accountStoreModule,
                viewModelsModule,
                timeFormatterModule,
                markdownModule
            ))
        }
    }
}