package com.rtuitlab.studo.utils

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
    private val clientDFOnlyDate = SimpleDateFormat("dd.MM.yyyy")

    @SuppressLint("SimpleDateFormat")
    private val clientDFOnlyTime = SimpleDateFormat("HH:mm")

    private val separatorSpannable = SpannableString(" — ")

    fun generateDateRangeFromDateTimeForAd(beginTime: String, endTime: String): SpannableStringBuilder {
        var clientDF = clientDF
        var startIndex = 5

        if (beginTime.contains("00:00:00.000") && endTime.contains("00:00:00.000")) { // If without time
            clientDF = clientDFOnlyDate
            startIndex = 0
        }

        val beginTimeSpannable = SpannableString(clientDF.format(serverDF.parse(beginTime)!!))
        val endTimeSpannable = SpannableString(clientDF.format(serverDF.parse(endTime)!!))

        beginTimeSpannable[startIndex..beginTimeSpannable.length] = StyleSpan(Typeface.BOLD)
        endTimeSpannable[startIndex..endTimeSpannable.length] = StyleSpan(Typeface.BOLD)

        return SpannableStringBuilder().append(beginTimeSpannable).append(separatorSpannable).append(endTimeSpannable)
    }

    fun generateDateFromDateTimeForComment(time: String): String {
        val commentDate = serverDF.parse(time)!!
        val currentDate = Calendar.getInstance().time
        val diff = currentDate.time - commentDate.time

        val dayTimestamp = 1000 * 60 * 60 * 24
        return if (diff > dayTimestamp) { // Difference more than day
            clientDFOnlyDate.format(commentDate)
        } else { // Difference lower than day
            clientDFOnlyTime.format(commentDate)
        }
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

    fun generateTimeRangeFromTimestamps(beginTime: Long, endTime: Long): String {
        val beginTimeStr = DateFormat.format("HH:mm", Calendar.getInstance().apply {
            timeInMillis = beginTime
        })
        val endTimeStr = DateFormat.format("HH:mm", Calendar.getInstance().apply {
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

    fun generateTimestampFromDateTime(dateTime: String): Long {
        return serverDF.parse(dateTime)!!.time
    }
}