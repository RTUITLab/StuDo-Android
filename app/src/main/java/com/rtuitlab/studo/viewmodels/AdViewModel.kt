package com.rtuitlab.studo.viewmodels

import android.app.Application
import android.text.SpannableStringBuilder
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rtuitlab.studo.DateTimeFormatter
import com.rtuitlab.studo.SingleLiveEvent
import com.rtuitlab.studo.account.AccountStorage
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.ads.AdsRepository
import com.rtuitlab.studo.server.general.ads.models.Ad
import com.yydcdut.markdown.MarkdownProcessor
import com.yydcdut.markdown.syntax.text.TextFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdViewModel(
    app: Application,
    private val adsRepo: AdsRepository,
    private val accStorage: AccountStorage,
    private val dateTimeFormatter: DateTimeFormatter
): AndroidViewModel(app) {

    var adId = ""

    val currentAd = ObservableField<Ad>()
    val spannedDescription = ObservableField<CharSequence>("")
    val creatorFullName = ObservableField("")
    val creatorAvatarText = ObservableField("")
    val adDateTimeText = ObservableField(SpannableStringBuilder(""))

    val isOwnAd = ObservableBoolean(false)

    private val _currentAdResource = SingleLiveEvent<Resource<Ad>>()
    val currentAdResource: LiveData<Resource<Ad>> = _currentAdResource

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

        viewModelScope.launch(Dispatchers.Default) {
            val markdownProcessor = MarkdownProcessor(getApplication())
            markdownProcessor.factory(TextFactory.create())
            spannedDescription.set(markdownProcessor.parse(ad.description))
        }

        ad.user?.let{ // User`s ad
            creatorFullName.set("${it.name} ${it.surname}")
            creatorAvatarText.set("${it.name.first()}${it.surname.first()}")

            isOwnAd.set(ad.userId == accStorage.user.id)
        } ?:run {
            ad.organization?.let { // Organization`s ad
                creatorFullName.set(it.name)
                creatorAvatarText.set(creatorFullName.get()!!.first().toString())
            }
        }

        adDateTimeText.set(dateTimeFormatter.generateDateRangeFromDateTimeForAd(ad.beginTime, ad.endTime))
    }
}