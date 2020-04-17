package com.rtuitlab.studo.viewmodels.resumes

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtuitlab.studo.utils.SingleLiveEvent
import com.rtuitlab.studo.account.AccountStorage
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.resumes.ResumesRepository
import com.rtuitlab.studo.server.general.resumes.models.CompactResume
import com.rtuitlab.studo.server.general.resumes.models.Resume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResumeViewModel(
    private val accStorage: AccountStorage,
    private val resumesRepo: ResumesRepository
): ViewModel() {

    lateinit var compactResume: CompactResume

    val currentResume = ObservableField<Resume>()

    val title = ObservableField("")
    val creatorFullName = ObservableField("")
    val creatorAvatarText = ObservableField("")

    var isOwnResume = false

    private val _currentResumeResource = SingleLiveEvent<Resource<Resume>>()
    val currentResumeResource: LiveData<Resource<Resume>> = _currentResumeResource

    fun loadResume() {
        viewModelScope.launch {
            _currentResumeResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                resumesRepo.getResume(compactResume.id)
            }

            if (response.status == Status.SUCCESS) {
                fillResumeData(response.data!!)
            }

            _currentResumeResource.value = response
        }
    }

    private val _deleteResumeResource = SingleLiveEvent<Resource<Unit>>()
    val deleteResumeResource: LiveData<Resource<Unit>> = _deleteResumeResource

    fun deleteResume() {
        viewModelScope.launch {
            _deleteResumeResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                resumesRepo.deleteResume(compactResume.id)
            }

            _deleteResumeResource.value = response
        }
    }

    fun fillResumeData() {
        title.set(compactResume.name)

        creatorFullName.set(compactResume.userName)
        val userNameSplited = compactResume.userName.split(" ")
        creatorAvatarText.set("${userNameSplited[0].first()}${userNameSplited[1].first()}")
    }

    private fun fillResumeData(resume: Resume) {
        currentResume.set(resume)

        title.set(resume.name)

        val user = resume.user
        creatorFullName.set("${user.name} ${user.surname}")
        creatorAvatarText.set("${user.name.first()}${user.surname.first()}")

        isOwnResume = resume.userId == accStorage.user.id
    }

}