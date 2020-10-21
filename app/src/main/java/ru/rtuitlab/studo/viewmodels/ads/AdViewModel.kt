package ru.rtuitlab.studo.viewmodels.ads

import android.app.Application
import android.text.SpannableStringBuilder
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.yydcdut.markdown.MarkdownProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rtuitlab.studo.account.AccountStorage
import ru.rtuitlab.studo.server.Resource
import ru.rtuitlab.studo.server.Status
import ru.rtuitlab.studo.server.general.ads.AdsRepository
import ru.rtuitlab.studo.server.general.ads.models.Ad
import ru.rtuitlab.studo.server.general.ads.models.CompactAd
import ru.rtuitlab.studo.utils.DateTimeFormatter
import ru.rtuitlab.studo.utils.SingleLiveEvent

class AdViewModel(
    app: Application,
    private val adsRepo: AdsRepository,
    private val accStorage: AccountStorage,
    private val dateTimeFormatter: DateTimeFormatter,
    private val markdownTextProcessor: MarkdownProcessor
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

    private val _deleteAdResource = SingleLiveEvent<Resource<Unit>>()
    val deleteAdResource: LiveData<Resource<Unit>> = _deleteAdResource

    fun deleteAd() {
        viewModelScope.launch {
            _deleteAdResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                adsRepo.deleteAd(compactAd.id)
            }

            _deleteAdResource.value = response
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

        viewModelScope.launch(Dispatchers.IO) {
            spannedDescription.set(markdownTextProcessor.parse(ad.description))
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