package ru.rtuitlab.studo.viewmodels.resumes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rtuitlab.studo.account.AccountStorage
import ru.rtuitlab.studo.server.Resource
import ru.rtuitlab.studo.server.general.resumes.ResumesRepository
import ru.rtuitlab.studo.server.general.resumes.models.CompactResume
import ru.rtuitlab.studo.utils.SingleLiveEvent
import java.io.Serializable

sealed class ResumesType: Serializable
object AllResumes: ResumesType()
object OwnResumes: ResumesType()
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
                    OwnResumes -> resumesRepo.getUserResumes(accStorage.user.id)
                    is UserResumes -> resumesRepo.getUserResumes(resumesType.userId)
                }
            }

            _resumesListResource.value = response
        }
    }
}