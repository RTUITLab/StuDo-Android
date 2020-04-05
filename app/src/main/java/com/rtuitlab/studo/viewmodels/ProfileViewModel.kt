package com.rtuitlab.studo.viewmodels

import androidx.databinding.ObservableField
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

    var userInitials =
        ObservableField("${currentUser?.name?.first() ?: ""}${currentUser?.surname?.first() ?: ""}")
    val name = ObservableField(currentUser?.name ?: "")
    val surname = ObservableField(currentUser?.surname ?: "")
    val email = ObservableField(currentUser?.email ?: "")
    val cardNumber = ObservableField(currentUser!!.studentCardNumber ?: "")

    private val _currentUserResource = SingleLiveEvent<Resource<User>>()
    val currentUserResource = _currentUserResource

    fun updateCurrentUser() {
        viewModelScope.launch {
            _currentUserResource.value = Resource.loading(null)
            val response = withContext(Dispatchers.IO) {
                userRepo.loadCurrentUser()
            }
            currentUser = response.data
            userInitials.set("${currentUser!!.name[0]}${currentUser!!.surname[0]}")
            name.set(currentUser!!.name)
            surname.set(currentUser!!.surname)
            email.set(currentUser!!.email)
            cardNumber.set(currentUser!!.studentCardNumber)
            _currentUserResource.value = response
        }
    }
}