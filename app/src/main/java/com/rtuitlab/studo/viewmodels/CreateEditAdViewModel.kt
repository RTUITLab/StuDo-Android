package com.rtuitlab.studo.viewmodels

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rtuitlab.studo.App
import com.rtuitlab.studo.DateTimeFormatter
import com.rtuitlab.studo.R
import com.rtuitlab.studo.SingleLiveEvent
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.general.ads.AdsRepository
import com.rtuitlab.studo.server.general.ads.models.Ad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class CreateEditAdViewModel(
    app: Application,
    private val dateTimeFormatter: DateTimeFormatter,
    private val adsRepo: AdsRepository
): AndroidViewModel(app) {

    val title = ObservableField("")
    val shortDesc = ObservableField("")
    val desc = ObservableField("")

    val dateText = ObservableField(getApplication<App>().getString(R.string.not_selected))
    val timeText = ObservableField(getApplication<App>().getString(R.string.not_selected))

    private var beginDate: Long = 0
    private var endDate: Long = 0

    val isTimeEnabled = ObservableBoolean(false)

    val isValid = ObservableBoolean(false)

    fun checkData() {
        title.set(title.get()?.trimStart())
        shortDesc.set(shortDesc.get()?.trimStart())
        desc.set(desc.get()?.trimStart())
        isValid.set(
            !title.get().isNullOrBlank() &&
                    !shortDesc.get().isNullOrBlank() &&
                    !desc.get().isNullOrBlank() &&
                    dateText.get() != getApplication<App>().getString(R.string.not_selected) /*&&
                    timeText.get() != getApplication<App>().getString(R.string.not_selected)*/
        )
    }

    private val _adResource = SingleLiveEvent<Resource<Ad>>()
    val adResource: LiveData<Resource<Ad>> = _adResource

    fun createAd() {
        viewModelScope.launch {
            _adResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                adsRepo.createAd(
                    title.get()!!, desc.get()!!, shortDesc.get()!!,
                    dateTimeFormatter.generateDateTimeFromTimestamp(beginDate, isTimeEnabled.get()),
                    dateTimeFormatter.generateDateTimeFromTimestamp(endDate, isTimeEnabled.get())
                )
            }

            _adResource.value = response
        }
    }

    fun setDateRange(dateRange: androidx.core.util.Pair<Long, Long>) {
        val beginDateCalendar = Calendar.getInstance().apply {
            timeInMillis = dateRange.first!!
        }
        val endDateCalendar = Calendar.getInstance().apply {
            timeInMillis = dateRange.second!!
        }

        val beginTimeCalendar = Calendar.getInstance().apply {
            timeInMillis = beginDate
        }
        val endTimeCalendar = Calendar.getInstance().apply {
            timeInMillis = endDate
        }

        beginDateCalendar.set(Calendar.HOUR_OF_DAY, beginTimeCalendar.get(Calendar.HOUR))
        beginDateCalendar.set(Calendar.MINUTE, beginTimeCalendar.get(Calendar.MINUTE))
        beginDateCalendar.set(Calendar.SECOND, beginTimeCalendar.get(Calendar.SECOND))
        beginDateCalendar.set(Calendar.MILLISECOND, 1)

        endDateCalendar.set(Calendar.HOUR_OF_DAY, endTimeCalendar.get(Calendar.HOUR))
        endDateCalendar.set(Calendar.MINUTE, endTimeCalendar.get(Calendar.MINUTE))
        endDateCalendar.set(Calendar.SECOND, endTimeCalendar.get(Calendar.SECOND))
        endDateCalendar.set(Calendar.MILLISECOND, 1)

        beginDate = beginDateCalendar.timeInMillis
        endDate = endDateCalendar.timeInMillis
        dateText.set(dateTimeFormatter.generateDateRangeFromTimestamps(beginDate, endDate))

        checkData()
    }
}