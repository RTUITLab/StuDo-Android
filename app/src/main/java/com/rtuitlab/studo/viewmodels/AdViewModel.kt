package com.rtuitlab.studo.viewmodels

import android.text.SpannableStringBuilder
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtuitlab.studo.DateTimeFormatter
import com.rtuitlab.studo.SingleLiveEvent
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.ads.AdsRepository
import com.rtuitlab.studo.server.general.ads.models.Ad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdViewModel(
    private val adsRepo: AdsRepository,
    private val dateTimeFormatter: DateTimeFormatter
): ViewModel() {

    var adId = ""

    private val _currentAdResource = SingleLiveEvent<Resource<Ad>>()
    val currentAdResource: LiveData<Resource<Ad>> = _currentAdResource

    val currentAd = ObservableField<Ad>()
    var creatorFullName = ObservableField("")
    var creatorAvatarText = ObservableField("")
    var adDateTimeText = ObservableField(SpannableStringBuilder(""))

    fun loadAd(adId: String = this.adId) {
        viewModelScope.launch {
            _currentAdResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                adsRepo.getAd(adId)
            }

            if (response.status == Status.SUCCESS) {
                fillAdData(response.data!!)
            }

            _currentAdResource.value = response
        }
    }

    private fun fillAdData(ad: Ad) {
        currentAd.set(ad)

        ad.user?.let{ // User`s ad
            creatorFullName.set("${it.name} ${it.surname}")
            creatorAvatarText.set("${it.name.first()}${it.surname.first()}")
        } ?:run {
            ad.organization?.let { // Organization`s ad
                creatorFullName.set(it.name)
                creatorAvatarText.set(creatorFullName.get()!!.first().toString())
            }
        }

        adDateTimeText.set(dateTimeFormatter.generateDateRangeFromDateTimeForAd(ad.beginTime, ad.endTime))
    }
}