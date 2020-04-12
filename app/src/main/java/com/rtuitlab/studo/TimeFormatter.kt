package com.rtuitlab.studo

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import androidx.core.text.set
import org.koin.dsl.module
import java.text.SimpleDateFormat

val timeFormatterModule = module {
    single { DateTimeFormatter() }
}

class DateTimeFormatter {

    @SuppressLint("SimpleDateFormat")
    private val serverDF = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")

    @SuppressLint("SimpleDateFormat")
    private val clientDF = SimpleDateFormat("HH:mm dd.MM.yyyy")

    @SuppressLint("SimpleDateFormat")
    private val clientDFWithoutTime = SimpleDateFormat("dd.MM.yyyy")

    private val separatorSpannable by lazy {
        val spannable = SpannableString(" — ")
        spannable[0..3] = StyleSpan(Typeface.BOLD)
        spannable
    }

    fun generateDateTimeAd(beginTime: String, endTime: String = ""): SpannableStringBuilder {
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

    fun generateDateTimeComment(time: String): SpannableStringBuilder {
        val result = SpannableStringBuilder()



        return result
    }
}