package ru.rtuitlab.studo.server.general.ads

import ru.rtuitlab.studo.server.Resource
import ru.rtuitlab.studo.server.ResponseHandler
import ru.rtuitlab.studo.server.general.ads.models.*

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



    suspend fun createAd(
        name: String,
        description: String,
        shortDescription: String,
        beginTime: String,
        endTime: String,
        organizationId: String? = null
    ): Resource<Ad> {
        return try {
            responseHandler.handleSuccess(adsApi.createAd(CreateAdRequest(
                name, description, shortDescription, beginTime, endTime, organizationId
            )))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun editAd(
        id: String,
        name: String,
        description: String,
        shortDescription: String,
        beginTime: String,
        endTime: String
    ): Resource<Ad> {
        return try {
            responseHandler.handleSuccess(adsApi.editAd(EditAdRequest(
                id, name, description, shortDescription, beginTime, endTime
            )))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun deleteAd(adId: String): Resource<Unit> {
        return try {
            responseHandler.handleSuccess(adsApi.deleteAd(adId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }



    suspend fun getFavouritesAds(): Resource<List<CompactAd>> {
        return try {
            responseHandler.handleSuccess(adsApi.getFavouritesAds())
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun addToFavourites(adId: String): Resource<Unit> {
        return try {
            responseHandler.handleSuccess(adsApi.addToFavourites(adId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun removeFromFavourites(adId: String): Resource<Unit> {
        return try {
            responseHandler.handleSuccess(adsApi.removeFromFavourites(adId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }



    suspend fun createComment(adId: String, commentText: String): Resource<Comment> {
        return try {
            responseHandler.handleSuccess(adsApi.createComment(adId, CreateCommentRequest(commentText)))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    suspend fun deleteComment(adId: String, commentId: String): Resource<String> {
        return try {
            responseHandler.handleSuccess(adsApi.deleteComment(adId, commentId))
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}