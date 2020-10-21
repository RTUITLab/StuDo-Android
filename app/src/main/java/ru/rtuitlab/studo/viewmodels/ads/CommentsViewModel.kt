package ru.rtuitlab.studo.viewmodels.ads

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rtuitlab.studo.server.Resource
import ru.rtuitlab.studo.server.general.ads.AdsRepository
import ru.rtuitlab.studo.server.general.ads.models.Ad
import ru.rtuitlab.studo.server.general.ads.models.Comment
import ru.rtuitlab.studo.utils.SingleLiveEvent

class CommentsViewModel(
    private val adsRepo: AdsRepository
): ViewModel() {

    lateinit var ad: Ad

    val commentText = ObservableField("")

    val isValid = ObservableBoolean(false)

    private val _createCommentResource =
        SingleLiveEvent<Resource<Comment>>()
    val createCommentResource: LiveData<Resource<Comment>> = _createCommentResource

    fun createComment() {
        viewModelScope.launch {
            _createCommentResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                adsRepo.createComment(ad.id, commentText.get()!!)
            }

            _createCommentResource.value = response
        }
    }

    fun checkData() {
        commentText.set(commentText.get()?.trimStart())
        isValid.set(!commentText.get().isNullOrBlank())
    }

    var commentIdForDeleting = ""

    private val _deleteCommentResource = SingleLiveEvent<Resource<String>>()
    val deleteCommentResource: LiveData<Resource<String>> = _deleteCommentResource

    fun deleteComment() {
        viewModelScope.launch {
            _deleteCommentResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                adsRepo.deleteComment(ad.id, commentIdForDeleting)
            }

            _deleteCommentResource.value = response
        }
    }
}