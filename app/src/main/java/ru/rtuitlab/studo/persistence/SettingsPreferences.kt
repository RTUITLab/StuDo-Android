package ru.rtuitlab.studo.persistence

import android.content.Context
import android.os.Build
import androidx.preference.PreferenceManager
import ru.rtuitlab.studo.R

class SettingsPreferences(
    private val context: Context
) {
    companion object {
        private const val THEME_KEY = "themeDropdown"
        private const val LANGUAGE_KEY = "languagesDropdown"
    }

    private val settingPref = PreferenceManager.getDefaultSharedPreferences(context)

    fun getSelectedTheme(): String = settingPref.getString(THEME_KEY, null) ?:run {
        val preferredTheme = getPreferredTheme()
        settingPref.edit()
            .putString(THEME_KEY, preferredTheme)
            .apply()
        preferredTheme
    }

    fun getSelectedLanguage(): String = settingPref.getString(LANGUAGE_KEY, null) ?:run {
        val currentSystemLang = getCurrentSystemLang()
        settingPref.edit()
            .putString(LANGUAGE_KEY, currentSystemLang)
            .apply()
        currentSystemLang
    }

    private fun getPreferredTheme() = context.resources.getStringArray(R.array.entry_themes).first()

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