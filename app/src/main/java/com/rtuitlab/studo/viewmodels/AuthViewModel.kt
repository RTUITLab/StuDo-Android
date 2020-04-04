package com.rtuitlab.studo.viewmodels

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rtuitlab.studo.*
import com.rtuitlab.studo.extensions.isEmail
import com.rtuitlab.studo.extensions.isNotEmail
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.auth.AuthRepository
import com.rtuitlab.studo.server.auth.models.ResetPasswordRequest
import com.rtuitlab.studo.server.auth.models.UserLoginRequest
import com.rtuitlab.studo.server.auth.models.UserLoginResponse
import com.rtuitlab.studo.server.auth.models.UserRegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.RuntimeException

class AuthViewModel(
    private val app: Application,
    private val authRepo: AuthRepository
) : AndroidViewModel(app) {

    var name = ""
    var nameError = ObservableField("")

    var surname = ""
    var surnameError = ObservableField("")

    var email = "test@gmail.com"
    var emailError = ObservableField("")

    var cardNumber = ""
    var cardNumberError = ObservableField("")

    var password = "123456"
    var passwordError = ObservableField("")

    var confirmPassword = ""
    var confirmPasswordError = ObservableField("")

    private val _loginResource = SingleLiveEvent<Resource<UserLoginResponse>>()
    val loginResource: LiveData<Resource<UserLoginResponse>> = _loginResource

    private val _registerResource = SingleLiveEvent<Resource<Unit>>()
    val registerResource: LiveData<Resource<Unit>> = _registerResource

    private val _resetResource = SingleLiveEvent<Resource<Unit>>()
    val resetResource: LiveData<Resource<Unit>> = _resetResource

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
                    response.data?.let {
                        currentUser = it.user
                        accessToken = it.accessToken
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
            if (password != confirmPassword) {
                confirmPasswordError.set(app.getString(R.string.not_equal_password_error))
                result = false
            } else {
                confirmPasswordError.set("")
            }
        }
        return result
    }

    fun resetPassword(email: String): Boolean {
        return if (email.isEmail()) {
            viewModelScope.launch {
                _resetResource.value = Resource.loading(null)
                val response = withContext(Dispatchers.IO) {
                    authRepo.resetPassword(ResetPasswordRequest(email))
                }
                _resetResource.value = response
            }
            true
        } else {
            false
        }
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