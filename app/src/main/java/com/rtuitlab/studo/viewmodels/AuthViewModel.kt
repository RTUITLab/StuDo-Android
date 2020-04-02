package com.rtuitlab.studo.viewmodels

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rtuitlab.studo.App
import com.rtuitlab.studo.R
import com.rtuitlab.studo.SingleLiveEvent
import com.rtuitlab.studo.currentUserWithToken
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.auth.AuthRepository
import com.rtuitlab.studo.server.auth.models.UserLoginRequest
import com.rtuitlab.studo.server.auth.models.UserLoginResponse
import com.rtuitlab.studo.server.auth.models.UserRegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(
    private val app: Application,
    private val authRepo: AuthRepository
) : AndroidViewModel(app) {

    var name = ""
    var nameError = ObservableField("")

    var surname = ""
    var surnameError = ObservableField("")

    var email = ""
    var emailError = ObservableField("")

    var cardNumber = ""
    var cardNumberError = ObservableField("")

    var password = ""
    var passwordError = ObservableField("")

    var confirmPassword = ""
    var confirmPasswordError = ObservableField("")

    private val _loginResource = SingleLiveEvent<Resource<UserLoginResponse>>()
    val loginResource: LiveData<Resource<UserLoginResponse>> = _loginResource

    private val _registerResource = SingleLiveEvent<Resource<Unit>>()
    val registerResource: LiveData<Resource<Unit>> = _registerResource

    fun login() {
        if (isLoginDataCorrect()) {
            viewModelScope.launch {
                _loginResource.value = Resource.loading(null)
                val response = withContext(Dispatchers.IO) {
                    authRepo.login(
                        UserLoginRequest(
                            email, password
                        )
                    )
                }
                if (response.status == Status.SUCCESS) {
                    currentUserWithToken = response.data
                }
                _loginResource.value = response
            }
        }
    }

    private fun isLoginDataCorrect(): Boolean {
        var result = true
        if (email.isEmpty()) {
            emailError.set(app.getString(R.string.empty_field_error))
            result = false
        } else {
            emailError.set("")
        }
        if (password.length < 6) {
            if (password.isEmpty()) {
                passwordError.set(app.getString(R.string.empty_field_error))
            } else {
                passwordError.set(app.getString(R.string.small_password_error))
            }
            result = false
        } else {
            passwordError.set("")
        }
        return result
    }

    fun register() {
        if (isRegisterDataCorrect()) {
            viewModelScope.launch {
                _registerResource.value = Resource.loading(null)
                val response = withContext(Dispatchers.IO) {
                    authRepo.register(
                        UserRegisterRequest(
                            name, surname, email, cardNumber, password, confirmPassword
                        )
                    )
                }
                _registerResource.value = response
            }
        }
    }

    private fun isRegisterDataCorrect(): Boolean {
        var result = true
        if (name.isEmpty()) {
            nameError.set(app.getString(R.string.empty_field_error))
            result = false
        } else {
            nameError.set("")
        }
        if (surname.isEmpty()) {
            surnameError.set(app.getString(R.string.empty_field_error))
            result = false
        } else {
            surnameError.set("")
        }
        if (cardNumber.isEmpty()) {
            cardNumberError.set(app.getString(R.string.empty_field_error))
            result = false
        } else {
            cardNumberError.set("")
        }
        if (!isLoginDataCorrect()) {
            result = false
        }
        if (confirmPassword.isEmpty()) {
            confirmPasswordError.set(app.getString(R.string.empty_field_error))
            result = false
        } else {
            confirmPasswordError.set("")
            if (password != confirmPassword) {
                confirmPasswordError.set(app.getString(R.string.not_equal_password_error))
                result = false
            } else {
                confirmPasswordError.set("")
            }
        }
        return result
    }

    fun clearErrors() {
        nameError.set("")
        surnameError.set("")
        emailError.set("")
        cardNumberError.set("")
        passwordError.set("")
        confirmPasswordError.set("")
    }
}