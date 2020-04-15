package com.rtuitlab.studo.diff_util

import androidx.recyclerview.widget.DiffUtil
import com.rtuitlab.studo.server.general.ads.models.Comment

class CommentsDiffUtilCallback (
    private val oldList: List<Comment>,
    private val newList: List<Comment>
): DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldComment = oldList[oldItemPosition]
        val newComment = newList[newItemPosition]
        return oldComment.id == newComment.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldComment = oldList[oldItemPosition]
        val newComment = newList[newItemPosition]
        return oldComment.text == newComment.text &&
                oldComment.commentTime == newComment.commentTime &&
                oldComment.authorId == newComment.authorId &&
                oldComment.author == newComment.author
    }

}