package com.rtuitlab.studo.server.general.ads

import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.ResponseHandler
import com.rtuitlab.studo.server.general.ads.models.CompactAd
import java.lang.Exception

class AdsRepository(
    private val adsApi: AdsApi,
    private val responseHandler: ResponseHandler
) {
    suspend fun getAllAds(): Resource<List<CompactAd>> {
        return try {
            responseHandler.handleSuccess(adsApi.getAllAds())
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}