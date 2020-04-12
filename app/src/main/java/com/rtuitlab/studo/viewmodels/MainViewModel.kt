package com.rtuitlab.studo.viewmodels

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import com.rtuitlab.studo.account.AccountStorage
import com.rtuitlab.studo.persistence.SettingsPreferences

class MainViewModel(
    private val settingsPref: SettingsPreferences,
    private val accStorage: AccountStorage
): ViewModel() {

    fun isLogged(): Boolean = accStorage.isLogged()

    fun checkTheme() {
        settingsPref.isDarkTheme()?.let { isDark ->
            if (isDark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        } ?: run {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}