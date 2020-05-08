package com.rtuitlab.studo.persistence

import android.content.Context
import androidx.preference.PreferenceManager

class SettingsPreferences(context: Context) {

    companion object {
        private const val DARK_THEME_KEY = "themeSwitch"
        private const val LANGUAGE_KEY = "languagesDropdown"
    }

    private val settingPref = PreferenceManager.getDefaultSharedPreferences(context)

    fun isDarkTheme(): Boolean {
        if (!settingPref.contains(DARK_THEME_KEY)) {
            settingPref.edit().putBoolean(DARK_THEME_KEY, false).apply()
        }
        return settingPref.getBoolean(DARK_THEME_KEY, false)
    }

    fun getSelectedLanguage(): String {
        if (!settingPref.contains(LANGUAGE_KEY)) {
            settingPref.edit().putString(LANGUAGE_KEY, "ru").apply()
        }
        return settingPref.getString(LANGUAGE_KEY, "ru") ?: "ru"
    }
}