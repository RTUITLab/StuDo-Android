package com.rtuitlab.studo.viewmodels.resumes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtuitlab.studo.utils.SingleLiveEvent
import com.rtuitlab.studo.account.AccountStorage
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.general.resumes.ResumesRepository
import com.rtuitlab.studo.server.general.resumes.models.CompactResume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.io.Serializable

sealed class ResumesType: Serializable
object AllResumes: ResumesType()
object MyResumes: ResumesType()
data class UserResumes(val userId: String): ResumesType()

class ResumesListViewModel(
    private val resumesRepo: ResumesRepository,
    private val accStorage: AccountStorage
): ViewModel() {

    private val _resumesListResource =
        SingleLiveEvent<Resource<List<CompactResume>>>()
    val resumesListResource: LiveData<Resource<List<CompactResume>>> = _resumesListResource

    fun loadResumesList(resumesType: ResumesType) {
        viewModelScope.launch {
            _resumesListResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                when(resumesType) {
                    AllResumes -> resumesRepo.getAllResumes()
                    MyResumes -> resumesRepo.getUserResumes(accStorage.user.id)
                    is UserResumes -> resumesRepo.getUserResumes(resumesType.userId)
                }
            }

            _resumesListResource.value = response
        }
    }
}