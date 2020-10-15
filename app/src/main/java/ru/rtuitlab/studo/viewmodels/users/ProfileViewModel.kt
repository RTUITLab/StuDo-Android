package ru.rtuitlab.studo.viewmodels.users

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rtuitlab.studo.App
import ru.rtuitlab.studo.R
import ru.rtuitlab.studo.account.AccountStorage
import ru.rtuitlab.studo.persistence.AuthPreferences
import ru.rtuitlab.studo.server.Resource
import ru.rtuitlab.studo.server.Status
import ru.rtuitlab.studo.server.general.users.UserRepository
import ru.rtuitlab.studo.server.general.users.models.User
import ru.rtuitlab.studo.utils.SingleLiveEvent

class ProfileViewModel(
    app: Application,
    private val userRepo: UserRepository,
    private val authPrefs: AuthPreferences,
    accStorage: AccountStorage
): AndroidViewModel(app) {

    var user = accStorage.user

    private val _currentUserResource =
        SingleLiveEvent<Resource<User>>()
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

    private val _changesSavedResource =
        SingleLiveEvent<Resource<User>>()
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
        name.set(name.get()?.trimStart())
        surname.set(surname.get()?.trimStart())
        cardNumber.set(cardNumber.get()?.trimStart())
        var result = (
                name.get() != user.name ||
                        surname.get() != user.surname ||
                        cardNumber.get() != user.studentCardNumber
                )
        if (name.get().isNullOrBlank()) {
            nameError.set(getApplication<App>().getString(R.string.empty_field_error))
            result = false
        } else {
            nameError.set("")
        }
        if (surname.get().isNullOrBlank()) {
            surnameError.set(getApplication<App>().getString(R.string.empty_field_error))
            result = false
        } else {
            surnameError.set("")
        }
        if (cardNumber.get().isNullOrBlank()) {
            cardNumberError.set(getApplication<App>().getString(R.string.empty_field_error))
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
        authPrefs.storeUser(user)
        userInitials.set("${user.name[0]}${user.surname[0]}")
        name.set(user.name)
        surname.set(user.surname)
        email.set(user.email)
        cardNumber.set(user.studentCardNumber)
    }

    fun logout() {
        authPrefs.removeUserData()
    }

    fun clearChanges() {
        fillUserData(user)
        checkUserData()
    }
}