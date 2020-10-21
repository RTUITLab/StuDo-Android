package ru.rtuitlab.studo.viewmodels.users

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rtuitlab.studo.account.AccountStorage
import ru.rtuitlab.studo.server.Resource
import ru.rtuitlab.studo.server.Status
import ru.rtuitlab.studo.server.general.users.UserRepository
import ru.rtuitlab.studo.server.general.users.models.User
import ru.rtuitlab.studo.utils.SingleLiveEvent

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
