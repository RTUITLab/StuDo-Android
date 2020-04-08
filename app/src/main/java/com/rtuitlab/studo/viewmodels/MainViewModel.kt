package com.rtuitlab.studo.viewmodels

import androidx.lifecycle.ViewModel
import com.rtuitlab.studo.account.AccountStorage

class MainViewModel(
    private val accStorage: AccountStorage
): ViewModel() {

    fun isLogged(): Boolean = accStorage.isLogged()
}