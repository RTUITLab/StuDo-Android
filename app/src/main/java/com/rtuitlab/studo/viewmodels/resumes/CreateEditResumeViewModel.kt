package com.rtuitlab.studo.viewmodels.resumes

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtuitlab.studo.utils.SingleLiveEvent
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.general.resumes.ResumesRepository
import com.rtuitlab.studo.server.general.resumes.models.Resume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable

sealed class CreateEditResume : Serializable
object CreateResume: CreateEditResume()
data class EditResume(val resume: Resume): CreateEditResume()

class CreateEditResumeViewModel(
    private val resumesRepo: ResumesRepository
): ViewModel() {

    private var resumeId = ""

    val title = ObservableField("")
    val desc = ObservableField("")

    val isValid = ObservableBoolean(false)

    fun checkData() {
        title.set(title.get()?.trimStart())
        desc.set(desc.get()?.trimStart())

        isValid.set(!title.get().isNullOrBlank() && !desc.get().isNullOrBlank())
    }

    private val _resumeResource =
        SingleLiveEvent<Resource<Resume>>()
    val resumeResource: LiveData<Resource<Resume>> = _resumeResource

    fun createResume() {
        viewModelScope.launch {
            _resumeResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                resumesRepo.createResume(title.get()!!, desc.get()!!)
            }

            _resumeResource.value = response
        }
    }

    fun editResume() {
        viewModelScope.launch {
            _resumeResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                resumesRepo.editResume(resumeId, title.get()!!, desc.get()!!)
            }

            _resumeResource.value = response
        }
    }

    fun fillResumeData(resume: Resume) {
        resumeId = resume.id
        title.set(resume.name)
        desc.set(resume.description)
        checkData()
    }
}