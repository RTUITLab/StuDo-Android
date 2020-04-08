package com.rtuitlab.studo.server.general.ads

import com.rtuitlab.studo.server.general.ads.models.CompactAd
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AdsApi {
    @GET("ad")
    suspend fun getAllAds(): List<CompactAd>

    @GET("ad/user/{userId}")
    suspend fun getUserAds(@Path("userId") userId : String): List<CompactAd>

    @GET("ad/bookmarks")
    suspend fun getBookmarkedAds(): List<CompactAd>

    @POST("ad/bookmarks/{adId}")
    suspend fun addToBookmarks(@Path("adId") adId : String)

    @DELETE("ad/bookmarks/{adId}")
    suspend fun removeFromBookmarks(@Path("adId") adId : String)
}