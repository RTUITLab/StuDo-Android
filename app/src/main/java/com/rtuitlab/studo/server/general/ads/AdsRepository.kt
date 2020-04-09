package com.rtuitlab.studo.server.general.ads

import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.ResponseHandler
import com.rtuitlab.studo.server.general.ads.models.Ad
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

    suspend fun getAd(adId: String): Resource<Ad> {
        return try {
            responseHandler.handleSuccess(adsApi.getAd(adId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun getUserAds(userId: String): Resource<List<CompactAd>> {
        return try {
            responseHandler.handleSuccess(adsApi.getUserAds(userId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun getBookmarkedAds(): Resource<List<CompactAd>> {
        return try {
            responseHandler.handleSuccess(adsApi.getBookmarkedAds())
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun addToBookmarks(adId: String): Resource<Unit> {
        return try {
            responseHandler.handleSuccess(adsApi.addToBookmarks(adId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun removeFromBookmarks(adId: String): Resource<Unit> {
        return try {
            responseHandler.handleSuccess(adsApi.removeFromBookmarks(adId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}