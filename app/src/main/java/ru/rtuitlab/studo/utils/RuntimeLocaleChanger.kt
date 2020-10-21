package ru.rtuitlab.studo.utils

import android.content.Context
import android.content.res.Configuration
import ru.rtuitlab.studo.persistence.SettingsPreferences
import java.util.*

object RuntimeLocaleChanger {

    private fun createLocaleFromSavedLanguage(context: Context): Locale {
        val settingsPrefs = SettingsPreferences(context)
        val lang = settingsPrefs.getSelectedLanguage()
        return Locale(lang)
    }

    fun wrapContext(context: Context): Context {
        val savedLocale = createLocaleFromSavedLanguage(context)
        Locale.setDefault(savedLocale)

        val newConfig = Configuration().apply { setLocale(savedLocale) }
		return context.createConfigurationContext(newConfig)
    }

    @Suppress("DEPRECATION")
    fun overrideLocale(context: Context) {
        val savedLocale = createLocaleFromSavedLanguage(context)
        Locale.setDefault(savedLocale)

        val newConfig = Configuration().apply { setLocale(savedLocale) }
        context.resources.updateConfiguration(newConfig, context.resources.displayMetrics)

        context.applicationContext.takeIf { it != context }?.let {
            it.resources.run { updateConfiguration(newConfig, displayMetrics) }
        }
    }
}