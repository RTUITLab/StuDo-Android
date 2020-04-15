package com.rtuitlab.studo.extensions

import androidx.fragment.app.Fragment
import com.rtuitlab.studo.ui.general.MainActivity

fun Fragment.mainActivity(): MainActivity {
    return requireActivity() as MainActivity
}