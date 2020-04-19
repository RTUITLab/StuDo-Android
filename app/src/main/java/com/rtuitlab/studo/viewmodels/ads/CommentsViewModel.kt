package com.rtuitlab.studo.viewmodels.ads

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtuitlab.studo.utils.SingleLiveEvent
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.general.ads.AdsRepository
import com.rtuitlab.studo.server.general.ads.models.Ad
import com.rtuitlab.studo.server.general.ads.models.Comment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private val _deleteCommentResource = SingleLiveEvent<Resource<String>>()
    val deleteCommentResource: LiveData<Resource<String>> = _deleteCommentResource

    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            _deleteCommentResource.value = Resource.loading(null)

            val response = withContext(Dispatchers.IO) {
                adsRepo.deleteComment(ad.id, commentId)
            }

            _deleteCommentResource.value = response
        }
    }
}