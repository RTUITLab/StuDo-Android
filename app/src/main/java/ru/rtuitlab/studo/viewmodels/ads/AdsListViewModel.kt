package ru.rtuitlab.studo.viewmodels.ads

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rtuitlab.studo.account.AccountStorage
import ru.rtuitlab.studo.server.Resource
import ru.rtuitlab.studo.server.Status
import ru.rtuitlab.studo.server.general.ads.AdsRepository
import ru.rtuitlab.studo.server.general.ads.models.CompactAd
import ru.rtuitlab.studo.utils.SingleLiveEvent
import java.io.Serializable

sealed class AdsType : Serializable
object AllAds: AdsType()
object FavouritesAds : AdsType()
object OwnAds: AdsType()
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
                    FavouritesAds -> adsRepo.getFavouritesAds()
                    is OwnAds -> adsRepo.getUserAds(accStorage.user.id)
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
                    adsRepo.addToFavourites(compactAd.id)
                } else {
                    adsRepo.removeFromFavourites(compactAd.id)
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