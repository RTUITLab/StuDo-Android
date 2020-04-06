package com.rtuitlab.studo.viewmodels

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rtuitlab.studo.R
import com.rtuitlab.studo.SingleLiveEvent
import com.rtuitlab.studo.currentUser
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.user.UserRepository
import com.rtuitlab.studo.server.general.user.models.ChangeUserInfoRequest
import com.rtuitlab.studo.server.general.user.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val app: Application,
    private val userRepo: UserRepository
): AndroidViewModel(app) {

    private val _currentUserResource = SingleLiveEvent<Resource<User>>()
    val currentUserResource = _currentUserResource

    var userInitials =
        ObservableField("${currentUser?.name?.first() ?: ""}${currentUser?.surname?.first() ?: ""}")

    val name = ObservableField(currentUser?.name ?: "")
    val nameError = ObservableField("")

    val surname = ObservableField(currentUser?.surname ?: "")
    val surnameError = ObservableField("")

    val cardNumber = ObservableField(currentUser!!.studentCardNumber ?: "")
    val cardNumberError = ObservableField("")

    val email = ObservableField(currentUser?.email ?: "")

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
                name.get() != currentUser!!.name ||
                        surname.get() != currentUser!!.surname ||
                        cardNumber.get() != currentUser!!.studentCardNumber
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
                userRepo.changeUserInfo(ChangeUserInfoRequest(
                    currentUser!!.id,
                    name.get()!!,
                    surname.get()!!,
                    cardNumber.get()!!
                ))
            }
            if (response.status == Status.SUCCESS) {
                fillUserData(response.data!!)
                isUserDataChanged.set(false)
            }
            _changesSavedResource.value = response
        }
    }

    private fun fillUserData(user: User) {
        currentUser = user
        userInitials.set("${user.name[0]}${user.surname[0]}")
        name.set(user.name)
        surname.set(user.surname)
        email.set(user.email)
        cardNumber.set(user.studentCardNumber)
    }
}