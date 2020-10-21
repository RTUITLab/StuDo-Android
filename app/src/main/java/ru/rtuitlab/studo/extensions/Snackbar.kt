package ru.rtuitlab.studo.extensions

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.shortSnackbar(message: String) = snackbar(message, Snackbar.LENGTH_SHORT)

fun View.longSnackbar(message: String) = snackbar(message, Snackbar.LENGTH_LONG)

fun View.snackbar(message: String, duration: Int) = Snackbar.make(this, message, duration)