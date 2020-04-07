package com.rtuitlab.studo.viewmodels

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rtuitlab.studo.*
import com.rtuitlab.studo.extensions.isEmail
import com.rtuitlab.studo.extensions.isNotEmail
import com.rtuitlab.studo.persistence.EncryptedPreferences
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.auth.AuthRepository
import com.rtuitlab.studo.server.auth.models.UserLoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.RuntimeException

class AuthViewModel(
    private val app: Application,
    private val authRepo: AuthRepository,
    private val encryptedPrefs: EncryptedPreferences
) : AndroidViewModel(app) {

    var name = ""
    var nameError = ObservableField("")

    var surname = ""
    var surnameError = ObservableField("")

    var email = "almostroll@yandex.ru"
    var emailError = ObservableField("")

    var cardNumber = ""
    var cardNumberError = ObservableField("")

    var password = "123456"
    var passwordError = ObservableField("")

    var confirmPassword = ""
    var confirmPasswordError = ObservableField("")

    private val _loginResource = SingleLiveEvent<Resource<UserLoginResponse>>()
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
                        encryptedPrefs.storeUser(it.user)
                        encryptedPrefs.storeAccessToken(it.accessToken)
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
            emailError.set(app.getString(R.string.wrong_email_error))
            result = false
        } else {
            emailError.set("")
        }
        if (password.length < 6) {
            passwordError.set(app.getString(R.string.small_password_error))
            result = false
        } else {
            passwordError.set("")
        }
        return result
    }

    private val _registerResource = SingleLiveEvent<Resource<Unit>>()
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
            if (password != confirmPassword) {
                confirmPasswordError.set(app.getString(R.string.not_equal_password_error))
                result = false
            } else {
                confirmPasswordError.set("")
            }
        }
        return result
    }

    var resetEmail = ""

    private val _resetResource = SingleLiveEvent<Resource<Unit>>()
    val resetResource: LiveData<Resource<Unit>> = _resetResource

    fun resetPassword() {
        viewModelScope.launch {
            _resetResource.value = Resource.loading(null)
            val response = withContext(Dispatchers.IO) {
                authRepo.resetPassword(email)
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