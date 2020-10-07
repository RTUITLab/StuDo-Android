package com.rtuitlab.studo.viewmodels

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import com.rtuitlab.studo.App
import com.rtuitlab.studo.R
import com.rtuitlab.studo.account.AccountStorage
import com.rtuitlab.studo.persistence.SettingsPreferences

class MainViewModel(
    app: Application,
    private val settingsPref: SettingsPreferences,
    private val accStorage: AccountStorage
): AndroidViewModel(app) {

    fun isLogged(): Boolean = accStorage.isLogged()

    fun checkTheme() {
        val themes = getApplication<App>().resources.getStringArray(R.array.entry_themes)
        val selectedTheme = when(settingsPref.getSelectedTheme()) {
            themes[0] -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            themes[1] -> AppCompatDelegate.MODE_NIGHT_NO
            themes[2] -> AppCompatDelegate.MODE_NIGHT_YES
            else -> throw RuntimeException("Illegal value in preferences")
        }
        AppCompatDelegate.setDefaultNightMode(selectedTheme)
    }
}