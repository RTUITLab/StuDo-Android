package com.rtuitlab.studo.viewmodels.users

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtuitlab.studo.account.AccountStorage
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.users.UserRepository
import com.rtuitlab.studo.server.general.users.models.User
import com.rtuitlab.studo.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OtherUserViewModel(
    private val userRepo: UserRepository,
    private val accStorage: AccountStorage
): ViewModel() {

    lateinit var userId: String

    val userFullName = ObservableField("")
    val userInitials = ObservableField("")
    val userEmail = ObservableField("")

    private val _userResource = SingleLiveEvent<Resource<User>>()
    val userResource: LiveData<Resource<User>> = _userResource

    fun loadUser() {
        viewModelScope.launch {
            _userResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                userRepo.loadUser(userId)
            }

            if (response.status == Status.SUCCESS) {
                fillUserData(response.data!!)
            }

            _userResource.value = response
        }
    }

    fun fillUserData(user: User) {
        userFullName.set("${user.name} ${user.surname}")
        userInitials.set("${user.name.first()}${user.surname.first()}")
        userEmail.set(user.email)
    }

    fun isOwnProfile() = userId == accStorage.user.id
}
