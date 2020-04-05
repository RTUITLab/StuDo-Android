package com.rtuitlab.studo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtuitlab.studo.SingleLiveEvent
import com.rtuitlab.studo.currentUser
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.general.user.UserRepository
import com.rtuitlab.studo.server.general.user.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val userRepo: UserRepository
): ViewModel() {

    var userInitials = "${currentUser!!.name[0]}${currentUser!!.surname[0]}"

    private val _currentUserResource = SingleLiveEvent<Resource<User>>()
    val currentUserResource = _currentUserResource

    fun updateCurrentUser() {
        viewModelScope.launch {
            _currentUserResource.value = Resource.loading(null)
            val response = withContext(Dispatchers.IO) {
                userRepo.loadCurrentUser()
            }
            currentUser = response.data
            userInitials = "${currentUser!!.name[0]}${currentUser!!.surname[0]}"
            _currentUserResource.value = response
        }
    }
}