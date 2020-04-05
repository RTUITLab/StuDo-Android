package com.rtuitlab.studo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtuitlab.studo.SingleLiveEvent
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.general.resumes.ResumesRepository
import com.rtuitlab.studo.server.general.resumes.models.CompactResume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResumesViewModel(
    private val resumesRepo: ResumesRepository
): ViewModel() {

    private val _resumesListResource = SingleLiveEvent<Resource<List<CompactResume>>>()
    val resumesListResource: LiveData<Resource<List<CompactResume>>> = _resumesListResource

    fun loadAllResumes() {
        viewModelScope.launch {
            _resumesListResource.value = Resource.loading(null)
            val response = withContext(Dispatchers.IO) {
                resumesRepo.getAllResumes()
            }
            _resumesListResource.value = response
        }
    }
}