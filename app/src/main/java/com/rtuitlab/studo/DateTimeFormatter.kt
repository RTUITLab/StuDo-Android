package com.rtuitlab.studo

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.format.DateFormat
import android.text.style.StyleSpan
import androidx.core.text.set
import org.koin.dsl.module
import java.text.SimpleDateFormat
import java.util.*

val timeFormatterModule = module {
    single { DateTimeFormatter() }
}

class DateTimeFormatter {

    @SuppressLint("SimpleDateFormat")
    private val serverDF = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")

    @SuppressLint("SimpleDateFormat")
    private val serverDFWithoutTime = SimpleDateFormat("yyyy-MM-dd'T'00:00:00.000")

    @SuppressLint("SimpleDateFormat")
    private val clientDF = SimpleDateFormat("HH:mm dd.MM.yyyy")

    @SuppressLint("SimpleDateFormat")
    private val clientDFWithoutTime = SimpleDateFormat("dd.MM.yyyy")

    private val separatorSpannable = SpannableString(" — ")

    fun generateDateRangeFromDateTimeForAd(beginTime: String, endTime: String): SpannableStringBuilder {
        var clientDF = clientDF
        var startIndex = 5

        if (beginTime.contains("00:00:00.000") && endTime.contains("00:00:00.000")) { // If without time
            clientDF = clientDFWithoutTime
            startIndex = 0
        }

        val beginTimeSpannable = SpannableString(clientDF.format(serverDF.parse(beginTime)!!))
        val endTimeSpannable = SpannableString(clientDF.format(serverDF.parse(endTime)!!))

        beginTimeSpannable[startIndex..beginTimeSpannable.length] = StyleSpan(Typeface.BOLD)
        endTimeSpannable[startIndex..endTimeSpannable.length] = StyleSpan(Typeface.BOLD)

        return SpannableStringBuilder().append(beginTimeSpannable).append(separatorSpannable).append(endTimeSpannable)
    }

    fun generateDateFromDateTimeForComment(time: String): SpannableStringBuilder {
        val result = SpannableStringBuilder()



        return result
    }

    fun generateDateRangeFromTimestamps(beginTime: Long, endTime: Long): String {
        val beginTimeStr = DateFormat.format("dd.MM.yyyy", Calendar.getInstance().apply {
            timeInMillis = beginTime
        })
        val endTimeStr = DateFormat.format("dd.MM.yyyy", Calendar.getInstance().apply {
            timeInMillis = endTime
        })
        return "$beginTimeStr${separatorSpannable}$endTimeStr"
    }

    fun generateDateTimeFromTimestamp(time: Long, isTimeEnabled: Boolean): String {
        val date = Date().apply {
            this.time = time
        }
        return if (isTimeEnabled) {
            serverDF.format(date)
        } else {
            serverDFWithoutTime.format(date)
        }
    }
}