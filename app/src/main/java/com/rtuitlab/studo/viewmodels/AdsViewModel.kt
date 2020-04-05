package com.rtuitlab.studo.viewmodels

import androidx.lifecycle.*
import com.rtuitlab.studo.SingleLiveEvent
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.general.ads.AdsRepository
import com.rtuitlab.studo.server.general.ads.models.CompactAd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdsViewModel(
    private val adsRepo: AdsRepository
): ViewModel() {

    private val _adsListResource = SingleLiveEvent<Resource<List<CompactAd>>>()
    val adsListResource: LiveData<Resource<List<CompactAd>>> = _adsListResource

    fun loadAdsList() {
        viewModelScope.launch {
            _adsListResource.value = Resource.loading(null)
            val response = withContext(Dispatchers.IO) {
                adsRepo.getAllAds()
            }
            _adsListResource.value = response
        }
    }
}