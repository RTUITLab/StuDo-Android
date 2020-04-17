package com.rtuitlab.studo.server.general.ads

import com.rtuitlab.studo.server.general.ads.models.*
import retrofit2.http.*

interface AdsApi {
    @GET("ad")
    suspend fun getAllAds(): List<CompactAd>

    @GET("ad/{adId}")
    suspend fun getAd(@Path("adId") adId: String): Ad

    @GET("ad/user/{userId}")
    suspend fun getUserAds(@Path("userId") userId : String): List<CompactAd>



    @POST("ad")
    suspend fun createAd(@Body body: CreateAdRequest): Ad

    @PUT("ad")
    suspend fun editAd(@Body body: EditAdRequest): Ad

    @DELETE("ad/{adId}")
    suspend fun deleteAd(@Path("adId") adId : String)


    @GET("ad/bookmarks")
    suspend fun getBookmarkedAds(): List<CompactAd>

    @POST("ad/bookmarks/{adId}")
    suspend fun addToBookmarks(@Path("adId") adId : String)

    @DELETE("ad/bookmarks/{adId}")
    suspend fun removeFromBookmarks(@Path("adId") adId : String)



    @POST("ad/comment/{adId}")
    suspend fun createComment(@Path("adId") adId : String, @Body body: CreateCommentRequest): Comment

    @DELETE("ad/comment/{adId}/{commentId}")
    suspend fun deleteComment(@Path("adId") adId : String, @Path("commentId") commentId: String): String
}