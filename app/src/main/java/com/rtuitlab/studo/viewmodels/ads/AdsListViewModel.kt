package com.rtuitlab.studo.viewmodels.ads

import androidx.lifecycle.*
import com.rtuitlab.studo.utils.SingleLiveEvent
import com.rtuitlab.studo.account.AccountStorage
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.ads.AdsRepository
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

    private val _adsListResource =
        SingleLiveEvent<Resource<List<CompactAd>>>()
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

    private val _favouritesResource = SingleLiveEvent<Resource<CompactAd>>()
    val favouritesResource: LiveData<Resource<CompactAd>> = _favouritesResource

    fun toggleFavourite(compactAd: CompactAd) {
        viewModelScope.launch {
            _favouritesResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                if (compactAd.isFavourite) {
                    adsRepo.addToBookmarks(compactAd.id)
                } else {
                    adsRepo.removeFromBookmarks(compactAd.id)
                }
            }

            if (response.status == Status.SUCCESS) {
                _favouritesResource.value = Resource.success(compactAd)
            } else {
                val checkResponse =  withContext(Dispatchers.IO) {
                    adsRepo.getAd(compactAd.id)
                }

                if (checkResponse.status == Status.SUCCESS &&
                    checkResponse.data!!.isFavourite == compactAd.isFavourite) {
                    _favouritesResource.value = Resource.success(compactAd)
                } else {
                    _favouritesResource.value = Resource.error(
                        response.message!!,
                        compactAd.apply { isFavourite = !isFavourite }
                    )
                }
            }
        }
    }
}