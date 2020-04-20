package com.rtuitlab.studo.extensions

import android.graphics.Color
import android.widget.Button
import androidx.annotation.StringRes
import com.github.razir.progressbutton.DrawableButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress

fun Button.hideProgress(@StringRes newTextRes: Int) {
    this.hideProgress(newTextRes)
    this.isClickable = true
}

fun Button.showProgress() {
    this.showProgress{
        gravity = DrawableButton.GRAVITY_CENTER
        progressColor = Color.WHITE
    }
    this.isClickable = false
}