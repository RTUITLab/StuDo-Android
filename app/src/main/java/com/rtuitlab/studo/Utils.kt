package com.rtuitlab.studo

import android.util.Patterns

fun String.isEmail() = isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
fun String.isNotEmail() = !isEmail()