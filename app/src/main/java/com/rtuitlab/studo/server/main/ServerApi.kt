package com.rtuitlab.studo.server.main

import com.rtuitlab.studo.server.main.models.CompactAd
import com.rtuitlab.studo.server.main.models.CompactResume
import retrofit2.http.GET

interface ServerApi {
    @GET("ad")
    suspend fun getAllAds() : List<CompactAd>

    @GET("resumes")
    suspend fun getAllResumes() : List<CompactResume>
}