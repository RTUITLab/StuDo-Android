package com.rtuitlab.studo.server.main

import com.rtuitlab.studo.server.main.models.CompactAd
import retrofit2.http.GET

interface ServerApi {
    @GET("ad")
    suspend fun getAllAds() : List<CompactAd>
}