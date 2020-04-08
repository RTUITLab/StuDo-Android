package com.rtuitlab.studo.viewmodels

import androidx.lifecycle.*
import com.rtuitlab.studo.SingleLiveEvent
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.ads.AdsRepository
import com.rtuitlab.studo.server.general.ads.models.CompactAdWithBookmark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable

sealed class AdsType : Serializable
object AllAds: AdsType()
object BookmarkedAds : AdsType()
data class UserAds(val userId: String): AdsType()

class AdsViewModel(
    private val adsRepo: AdsRepository
): ViewModel() {

    private val _adsListResource = SingleLiveEvent<Resource<List<CompactAdWithBookmark>>>()
    val adsListResource: LiveData<Resource<List<CompactAdWithBookmark>>> = _adsListResource

    fun loadAdsList(adsType: AdsType) {
        viewModelScope.launch {
            _adsListResource.value = Resource.loading(null)
            val response = withContext(Dispatchers.IO) {
                val bookmarkedAdsList = adsRepo.getBookmarkedAds()
                val adsList = when(adsType) {
                    AllAds -> adsRepo.getAllAds()
                    BookmarkedAds -> bookmarkedAdsList
                    is UserAds -> adsRepo.getUserAds(adsType.userId)
                }
                if (adsList.status == Status.SUCCESS && bookmarkedAdsList.status == Status.SUCCESS) {
                    if (adsType == BookmarkedAds) {
                        Resource.success(adsList.data!!.map {
                            CompactAdWithBookmark(it, true)
                        })
                    } else {
                        Resource.success(adsList.data!!.map {
                            CompactAdWithBookmark(it, bookmarkedAdsList.data!!.contains(it))
                        })
                    }
                } else {
                    Resource.error(adsList.message?: bookmarkedAdsList.message!!, null)
                }
            }
            _adsListResource.value = response
        }
    }

    private val _bookmarksResource = SingleLiveEvent<Resource<CompactAdWithBookmark>>()
    val bookmarksResource: LiveData<Resource<CompactAdWithBookmark>> = _bookmarksResource

    fun toggleBookmark(compactAdWithBookmark: CompactAdWithBookmark) {
        viewModelScope.launch {
            _bookmarksResource.value = Resource.loading(null)
            val response = withContext(Dispatchers.IO) {
                if (compactAdWithBookmark.isBookmarked) {
                    adsRepo.addToBookmarks(compactAdWithBookmark.ad.id)
                } else {
                    adsRepo.removeFromBookmarks(compactAdWithBookmark.ad.id)
                }
            }
            if (response.status == Status.SUCCESS) {
                _bookmarksResource.value = Resource.success(compactAdWithBookmark)
            } else {
                _bookmarksResource.value = Resource.error(
                    response.message!!,
                    compactAdWithBookmark.apply { isBookmarked = !isBookmarked }
                )
            }
        }
    }
}