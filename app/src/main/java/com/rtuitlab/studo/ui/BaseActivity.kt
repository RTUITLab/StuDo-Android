package com.rtuitlab.studo.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.rtuitlab.studo.utils.RuntimeLocaleChanger

abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(RuntimeLocaleChanger.wrapContext(base))
    }
}