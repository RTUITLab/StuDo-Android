package com.rtuitlab.studo.viewmodels

import androidx.lifecycle.*
import com.rtuitlab.studo.SingleLiveEvent
import com.rtuitlab.studo.extensions.isEmail
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.general.users.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Universal [AndroidViewModel] for change email or password
 */
class AccountChangesDialogsViewModel(
    private val userRepo: UserRepository
): ViewModel() {

    var old = ""
    var new = ""
    var confirm = ""

    private val _changeEmailResource = SingleLiveEvent<Resource<Unit>>()
    val changeEmailResource: LiveData<Resource<Unit>> = _changeEmailResource

    fun changeEmail() {
        viewModelScope.launch {
            _changeEmailResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                userRepo.changeEmail(old, new)
            }

            _changeEmailResource.value = response
        }
    }

    private val _changePasswordResource = SingleLiveEvent<Resource<Unit>>()
    val changePasswordResource: LiveData<Resource<Unit>> = _changePasswordResource

    fun changePassword() {
        viewModelScope.launch {
            _changePasswordResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                userRepo.changePassword(old, new)
            }

            _changePasswordResource.value = response
        }
    }

    fun isDataValid(type: DataType): Boolean {
        return when(type) {
            DataType.EMAIL -> {
                old.isEmail() && new.isEmail() && confirm.isEmail() && (new == confirm) && (old != new)
            }
            DataType.PASSWORD -> {
                old.length > 5 && new.length > 5 && confirm.length > 5 && (new == confirm) && (old != new)
            }
        }
    }

    fun clearData() {
        old = ""
        new = ""
        confirm = ""
    }

    enum class DataType{
        EMAIL, PASSWORD
    }
}