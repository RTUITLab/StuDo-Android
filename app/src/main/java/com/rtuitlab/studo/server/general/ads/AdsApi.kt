package com.rtuitlab.studo.server.general.ads

import com.rtuitlab.studo.server.general.ads.models.CompactAd
import retrofit2.http.GET

interface AdsApi {
    @GET("ad")
    suspend fun getAllAds() : List<CompactAd>
}