package com.rtuitlab.studo

import android.util.Log
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.rtuitlab.studo.custom_views.AvatarView

@BindingAdapter("handleErrorFrom")
fun TextInputLayout.handleError(error: String) {
    if (error.isNotEmpty()) {
        this.isErrorEnabled = true
        this.error = error
    } else {
        this.isErrorEnabled = false
    }
}

@BindingAdapter("bindText")
fun AvatarView.setText(text: String) {
    this.text = text
    this.invalidate()
}

@BindingAdapter("bindAfterTextChanged")
fun TextInputEditText.setAfterTextChangedListener(func: () -> Unit) {
    this.addTextChangedListener {
        doAfterTextChanged {
            func.invoke()
        }
    }
}

@BindingAdapter("isShow")
fun FloatingActionButton.isShow(isShow: Boolean) {
    if (isShow) {
        this.show()
    } else {
        this.hide()
    }
}