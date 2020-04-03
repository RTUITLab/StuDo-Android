package com.rtuitlab.studo.server.main

import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.ResponseHandler
import com.rtuitlab.studo.server.main.models.CompactAd
import org.koin.dsl.module
import java.lang.Exception

val serverModule = module {
    single { ServerRepository(get(), get()) }
}

class ServerRepository(
    private val serverApi: ServerApi,
    private val responseHandler: ResponseHandler
) {
    suspend fun getAllAds(): Resource<List<CompactAd>> {
        return try {
            responseHandler.handleSuccess(serverApi.getAllAds())
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}