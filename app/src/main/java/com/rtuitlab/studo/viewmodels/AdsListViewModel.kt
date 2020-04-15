package com.rtuitlab.studo.viewmodels

import androidx.lifecycle.*
import com.rtuitlab.studo.SingleLiveEvent
import com.rtuitlab.studo.account.AccountStorage
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.ads.AdsRepository
import com.rtuitlab.studo.server.general.ads.models.AdIdWithIsFavourite
import com.rtuitlab.studo.server.general.ads.models.CompactAd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable

sealed class AdsType : Serializable
object AllAds: AdsType()
object BookmarkedAds : AdsType()
object MyAds: AdsType()
data class UserAds(val userId: String): AdsType()

class AdsListViewModel(
    private val adsRepo: AdsRepository,
    private val accStorage: AccountStorage
): ViewModel() {

    private val _adsListResource = SingleLiveEvent<Resource<List<CompactAd>>>()
    val adsListResource: LiveData<Resource<List<CompactAd>>> = _adsListResource

    fun loadAdsList(adsType: AdsType) {
        viewModelScope.launch {
            _adsListResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                when(adsType) {
                    AllAds -> adsRepo.getAllAds()
                    BookmarkedAds -> adsRepo.getBookmarkedAds()
                    is MyAds -> adsRepo.getUserAds(accStorage.user.id)
                    is UserAds -> adsRepo.getUserAds(adsType.userId)
                }
            }

            _adsListResource.value = response
        }
    }

    private val _favouritesResource = SingleLiveEvent<Resource<AdIdWithIsFavourite>>()
    val favouritesResource: LiveData<Resource<AdIdWithIsFavourite>> = _favouritesResource

    fun toggleFavourite(adIdWithIsFavourite: AdIdWithIsFavourite) {
        viewModelScope.launch {
            _favouritesResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                if (adIdWithIsFavourite.isFavourite) {
                    adsRepo.addToBookmarks(adIdWithIsFavourite.id)
                } else {
                    adsRepo.removeFromBookmarks(adIdWithIsFavourite.id)
                }
            }

            if (response.status == Status.SUCCESS) {
                _favouritesResource.value = Resource.success(adIdWithIsFavourite)
            } else {
                val checkResponse =  withContext(Dispatchers.IO) {
                    adsRepo.getAd(adIdWithIsFavourite.id)
                }

                if (checkResponse.status == Status.SUCCESS &&
                    checkResponse.data!!.isFavourite == adIdWithIsFavourite.isFavourite) {
                    _favouritesResource.value = Resource.success(adIdWithIsFavourite)
                } else {
                    _favouritesResource.value = Resource.error(
                        response.message!!,
                        adIdWithIsFavourite.apply { isFavourite = !isFavourite }
                    )
                }
            }
        }
    }

    fun isOwnAd(creatorId: String): Boolean {
        return creatorId == accStorage.user.id
    }
}