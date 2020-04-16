package com.rtuitlab.studo.utils

import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints

class DateRangeValidator(
    private val minDate: Long,
    private val maxDate: Long = Long.MAX_VALUE
) : CalendarConstraints.DateValidator{

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong()
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {}

    override fun describeContents() = 0

    override fun isValid(date: Long): Boolean {
        return !(minDate > date || maxDate < date)
    }

    companion object CREATOR : Parcelable.Creator<DateRangeValidator> {
        override fun createFromParcel(parcel: Parcel): DateRangeValidator {
            return DateRangeValidator(parcel)
        }

        override fun newArray(size: Int): Array<DateRangeValidator?> {
            return arrayOfNulls(size)
        }
    }

}