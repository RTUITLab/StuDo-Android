package com.rtuitlab.studo.persistence

import android.content.Context
import androidx.preference.PreferenceManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val settingsPrefModule = module {
    single { SettingsPreferences(androidContext()) }
}

class SettingsPreferences(context: Context) {

    private val themeKey = "themeSwitch"
    private val languageKey = "languagesDropdown"

    private val settingPref = PreferenceManager.getDefaultSharedPreferences(context)

    fun isDarkTheme(): Boolean? {
        return if (settingPref.contains(themeKey)) {
            settingPref.getBoolean(themeKey, true)
        } else { null }
    }

    fun getSelectedLanguage() = settingPref.getString(languageKey, "en") ?: "en"
}