package com.rtuitlab.studo.extensions

import android.content.Context
import android.widget.Toast

fun Context.shortToast(message: String?) = toast(message, Toast.LENGTH_SHORT)

fun Context.longToast(message: String?) = toast(message, Toast.LENGTH_LONG)

fun Context.toast(message: String?, duration: Int): Toast = Toast.makeText(this, message, duration)