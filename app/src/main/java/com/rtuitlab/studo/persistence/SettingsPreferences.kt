package com.rtuitlab.studo.persistence

import android.content.Context
import android.os.Build
import androidx.preference.PreferenceManager
import com.rtuitlab.studo.R

class SettingsPreferences(
    private val context: Context
) {
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
        return settingPref.getString(LANGUAGE_KEY, null) ?:run {
            val currentSystemLang = getCurrentSystemLang()
            settingPref.edit()
                .putString(LANGUAGE_KEY, currentSystemLang)
                .apply()
            currentSystemLang
        }
    }

    private fun getCurrentSystemLang(): String {
        val res = context.resources
        val config = res.configuration

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.locales.getFirstMatch(
                res.getStringArray(R.array.entry_languages)
            )?.language ?: "en"
        } else {
            @Suppress("DEPRECATION")
            config.locale.language
        }
    }
}