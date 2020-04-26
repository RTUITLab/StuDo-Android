package com.rtuitlab.studo.viewmodels.auth

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rtuitlab.studo.*
import com.rtuitlab.studo.extensions.isEmail
import com.rtuitlab.studo.extensions.isNotEmail
import com.rtuitlab.studo.persistence.AuthPreferences
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.auth.AuthRepository
import com.rtuitlab.studo.server.auth.models.UserLoginResponse
import com.rtuitlab.studo.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.RuntimeException

class AuthViewModel(
    app: Application,
    private val authRepo: AuthRepository,
    private val authPrefs: AuthPreferences
) : AndroidViewModel(app) {

    var name = ""
    val nameError = ObservableField("")

    var surname = ""
    val surnameError = ObservableField("")

    var email = ""
    val emailError = ObservableField("")

    var cardNumber = ""
    val cardNumberError = ObservableField("")

    var password = ""
    val passwordError = ObservableField("")

    var confirmPassword = ""
    val confirmPasswordError = ObservableField("")

    private val _loginResource =
        SingleLiveEvent<Resource<UserLoginResponse>>()
    val loginResource: LiveData<Resource<UserLoginResponse>> = _loginResource

    fun login() {
        if (isLoginDataCorrect()) {
            viewModelScope.launch {
                _loginResource.value = Resource.loading(null)

                val response = withContext(Dispatchers.IO) {
                    authRepo.login(email, password)
                }

                if (response.status == Status.SUCCESS) {
                    response.data?.let {
                        authPrefs.storeUser(it.user)
                        authPrefs.storeAccessToken(it.accessToken)
                        authPrefs.storeRefreshToken(it.refreshToken)
                    } ?:run {
                        throw RuntimeException("Not enough data in auth response")
                    }
                }

                _loginResource.value = response
            }
        }
    }

    private fun isLoginDataCorrect(): Boolean {
        var result = true
        if (email.isNotEmail()) {
            emailError.set(getApplication<App>().getString(R.string.wrong_email_error))
            result = false
        } else {
            emailError.set("")
        }
        if (password.length < 6) {
            passwordError.set(getApplication<App>().getString(R.string.small_password_error))
            result = false
        } else {
            passwordError.set("")
        }
        return result
    }

    private val _registerResource =
        SingleLiveEvent<Resource<Unit>>()
    val registerResource: LiveData<Resource<Unit>> = _registerResource

    fun register() {
        if (isRegisterDataCorrect()) {
            viewModelScope.launch {
                _registerResource.value = Resource.loading(null)

                val response = withContext(Dispatchers.IO) {
                    authRepo.register(name, surname, email, cardNumber, password, confirmPassword)
                }

                _registerResource.value = response
            }
        }
    }

    private fun isRegisterDataCorrect(): Boolean {
        var result = true
        if (name.isBlank()) {
            nameError.set(getApplication<App>().getString(R.string.empty_field_error))
            result = false
        } else {
            nameError.set("")
        }
        if (surname.isBlank()) {
            surnameError.set(getApplication<App>().getString(R.string.empty_field_error))
            result = false
        } else {
            surnameError.set("")
        }
        if (cardNumber.isBlank()) {
            cardNumberError.set(getApplication<App>().getString(R.string.empty_field_error))
            result = false
        } else {
            cardNumberError.set("")
        }
        if (!isLoginDataCorrect()) {
            result = false
        }
        if (confirmPassword.isBlank()) {
            confirmPasswordError.set(getApplication<App>().getString(R.string.empty_field_error))
            result = false
        } else {
            if (password != confirmPassword) {
                confirmPasswordError.set(getApplication<App>().getString(R.string.not_equal_password_error))
                result = false
            } else {
                confirmPasswordError.set("")
            }
        }
        return result
    }

    var resetEmail = ""

    private val _resetResource =
        SingleLiveEvent<Resource<Unit>>()
    val resetResource: LiveData<Resource<Unit>> = _resetResource

    fun resetPassword() {
        viewModelScope.launch {
            _resetResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                authRepo.resetPassword(resetEmail)
            }

            _resetResource.value = response
        }
    }

    fun isDataValid(): Boolean {
        return resetEmail.isEmail()
    }

    fun clearErrors() {
        nameError.set("")
        surnameError.set("")
        emailError.set("")
        cardNumberError.set("")
        passwordError.set("")
        confirmPasswordError.set("")
    }

    fun clearResetEmail() {
        resetEmail = ""
    }
}