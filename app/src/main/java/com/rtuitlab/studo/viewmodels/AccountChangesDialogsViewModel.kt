package com.rtuitlab.studo.viewmodels

import androidx.lifecycle.*
import com.rtuitlab.studo.extensions.isEmail
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.general.profile.UserRepository
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

    private val _changeEmailResource = MutableLiveData<Resource<Unit>>()
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

    fun isDataValid(): Boolean {
        return old.isEmail() && new.isEmail() && confirm.isEmail() && (new == confirm) && (old != new)
    }

    fun clearData() {
        old = ""
        new = ""
        confirm = ""
    }
}