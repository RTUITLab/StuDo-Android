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

class AdsViewModel(
    private val adsRepo: AdsRepository
): ViewModel() {

    private val _adsListResource = SingleLiveEvent<Resource<List<CompactAdWithBookmark>>>()
    val adsListResource: LiveData<Resource<List<CompactAdWithBookmark>>> = _adsListResource

    fun loadAdsList() {
        viewModelScope.launch {
            _adsListResource.value = Resource.loading(null)
            val response = withContext(Dispatchers.IO) {
                val ads = adsRepo.getAllAds()
                val bookmarkedAds = adsRepo.getBookmarkedAds()
                if (ads.status == Status.SUCCESS && bookmarkedAds.status == Status.SUCCESS) {
                    Resource.success(ads.data!!.map { CompactAdWithBookmark(it, bookmarkedAds.data!!.contains(it)) })
                } else {
                    Resource.error(ads.message?: bookmarkedAds.message!!, null)
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