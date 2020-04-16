package com.rtuitlab.studo.viewmodels.resumes

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtuitlab.studo.utils.SingleLiveEvent
import com.rtuitlab.studo.account.AccountStorage
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.resumes.ResumesRepository
import com.rtuitlab.studo.server.general.resumes.models.Resume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResumeViewModel(
    private val accStorage: AccountStorage,
    private val resumesRepo: ResumesRepository
): ViewModel() {

    var resumeId = ""

    val currentResume = ObservableField<Resume>()
    val creatorFullName = ObservableField("")
    val creatorAvatarText = ObservableField("")

    val isOwnResume = ObservableBoolean(false)

    private val _currentResumeResource =
        SingleLiveEvent<Resource<Resume>>()
    val currentResumeResource: LiveData<Resource<Resume>> = _currentResumeResource

    fun loadResume(resumeId: String = this.resumeId) {
        viewModelScope.launch {
            _currentResumeResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                resumesRepo.getResume(resumeId)
            }

            if (response.status == Status.SUCCESS) {
                fillResumeData(response.data!!)
            }

            _currentResumeResource.value = response
        }
    }

    private fun fillResumeData(resume: Resume) {
        currentResume.set(resume)

        val user = resume.user
        creatorFullName.set("${user.name} ${user.surname}")
        creatorAvatarText.set("${user.name.first()}${user.surname.first()}")

        isOwnResume.set(resume.userId == accStorage.user.id)
    }

}