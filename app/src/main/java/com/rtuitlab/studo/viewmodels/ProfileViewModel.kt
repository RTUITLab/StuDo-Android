package com.rtuitlab.studo.viewmodels

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rtuitlab.studo.R
import com.rtuitlab.studo.SingleLiveEvent
import com.rtuitlab.studo.account.AccountStore
import com.rtuitlab.studo.persistence.EncryptedPreferences
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.profile.UserRepository
import com.rtuitlab.studo.server.general.profile.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val app: Application,
    private val userRepo: UserRepository,
    private val encryptedPrefs: EncryptedPreferences,
    accStore: AccountStore
): AndroidViewModel(app) {

    var user = accStore.user

    private val _currentUserResource = SingleLiveEvent<Resource<User>>()
    val currentUserResource = _currentUserResource

    var userInitials =
        ObservableField("${user.name.first()}${user.surname.first()}")

    val name = ObservableField(user.name)
    val nameError = ObservableField("")

    val surname = ObservableField(user.surname)
    val surnameError = ObservableField("")

    val cardNumber = ObservableField(user.studentCardNumber ?: "")
    val cardNumberError = ObservableField("")

    val email = ObservableField(user.email)

    val isUserDataChanged = ObservableBoolean(false)

    private val _changesSavedResource = SingleLiveEvent<Resource<User>>()
    val changesSavedResource: LiveData<Resource<User>> = _changesSavedResource

    fun updateCurrentUser() {
        viewModelScope.launch {
            _currentUserResource.value = Resource.loading(null)
            val response = withContext(Dispatchers.IO) {
                userRepo.loadCurrentUser()
            }
            if (response.status == Status.SUCCESS) {
                fillUserData(response.data!!)
            }
            _currentUserResource.value = response
        }
    }

    fun checkUserData() {
        var result = (
                name.get() != user.name ||
                        surname.get() != user.surname ||
                        cardNumber.get() != user.studentCardNumber
                )
        if (name.get()!!.isEmpty()) {
            nameError.set(app.getString(R.string.empty_field_error))
            result = false
        } else {
            nameError.set("")
        }
        if (surname.get()!!.isEmpty()) {
            surnameError.set(app.getString(R.string.empty_field_error))
            result = false
        } else {
            surnameError.set("")
        }
        if (cardNumber.get()!!.isEmpty()) {
            cardNumberError.set(app.getString(R.string.empty_field_error))
            result = false
        } else {
            cardNumberError.set("")
        }
        isUserDataChanged.set(result)
    }

    fun saveUserDataChanges() {
        viewModelScope.launch {
            _changesSavedResource.value = Resource.loading(null)
            val response = withContext(Dispatchers.IO) {
                userRepo.changeUserInfo(
                    name.get()!!.trim(),
                    surname.get()!!.trim(),
                    cardNumber.get()!!.trim()
                )
            }
            if (response.status == Status.SUCCESS) {
                fillUserData(response.data!!)
                isUserDataChanged.set(false)
            }
            _changesSavedResource.value = response
        }
    }

    private fun fillUserData(user: User) {
        this.user = user
        encryptedPrefs.storeUser(user)
        userInitials.set("${user.name[0]}${user.surname[0]}")
        name.set(user.name)
        surname.set(user.surname)
        email.set(user.email)
        cardNumber.set(user.studentCardNumber)
    }

    fun logout() {
        encryptedPrefs.removeUserData()
    }

    fun clearChanges() {
        fillUserData(user)
        checkUserData()
    }
}