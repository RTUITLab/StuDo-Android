package com.rtuitlab.studo.viewmodels.ads

import android.app.Application
import android.text.SpannableStringBuilder
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rtuitlab.studo.utils.DateTimeFormatter
import com.rtuitlab.studo.utils.SingleLiveEvent
import com.rtuitlab.studo.account.AccountStorage
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.ads.AdsRepository
import com.rtuitlab.studo.server.general.ads.models.Ad
import com.rtuitlab.studo.server.general.ads.models.CompactAd
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

    lateinit var compactAd: CompactAd

    val currentAd = ObservableField<Ad>()

    val title = ObservableField("")
    val spannedDescription = ObservableField<CharSequence>("")
    val creatorFullName = ObservableField("")
    val creatorAvatarText = ObservableField("")
    val adDateTimeText = ObservableField(SpannableStringBuilder(""))

    var isOwnAd = false

    private val _currentAdResource =
        SingleLiveEvent<Resource<Ad>>()
    val currentAdResource: LiveData<Resource<Ad>> = _currentAdResource

    fun loadAd() {
        viewModelScope.launch {
            _currentAdResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                adsRepo.getAd(compactAd.id)
            }

            if (response.status == Status.SUCCESS) {
                fillAdData(response.data!!)
            }

            _currentAdResource.value = response
        }
    }

    fun fillAdData() {
        title.set(compactAd.name)

        compactAd.userId?.let{ // User`s ad
            creatorFullName.set(compactAd.userName)
            val userNameSplited = creatorFullName.get()!!.split(" ")
            creatorAvatarText.set("${userNameSplited[0].first()}${userNameSplited[1].first()}")

            isOwnAd = compactAd.userId == accStorage.user.id
        } ?:run {
            compactAd.organizationId?.let { // Organization`s ad
                creatorFullName.set(compactAd.organizationName)
                creatorAvatarText.set(creatorFullName.get()!!.first().toString())
            }
        }

        adDateTimeText.set(dateTimeFormatter.generateDateRangeFromDateTimeForAd(compactAd.beginTime, compactAd.endTime))
    }

    private fun fillAdData(ad: Ad) {
        currentAd.set(ad)

        title.set(ad.name)
        compactAd.isFavourite = ad.isFavourite

        viewModelScope.launch(Dispatchers.Default) {
            val markdownProcessor = MarkdownProcessor(getApplication())
            markdownProcessor.factory(TextFactory.create())
            spannedDescription.set(markdownProcessor.parse(ad.description))
        }

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