package com.rtuitlab.studo

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("handleErrorFrom")
fun TextInputLayout.handleError(error: String) {
    if (error.isNotEmpty()) {
        this.isErrorEnabled = true
        this.error = error
    } else {
        this.isErrorEnabled = false
    }
}