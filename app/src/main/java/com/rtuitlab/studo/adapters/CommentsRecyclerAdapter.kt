package com.rtuitlab.studo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rtuitlab.studo.DateTimeFormatter
import com.rtuitlab.studo.R
import com.rtuitlab.studo.custom_views.AvatarView
import com.rtuitlab.studo.diff_util.CommentsDiffUtilCallback
import com.rtuitlab.studo.server.general.ads.models.Comment
import kotlinx.android.synthetic.main.view_recycler_comment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.getKoin

class CommentsRecyclerAdapter(
    startData: List<Comment> = listOf()
): RecyclerView.Adapter<CommentsRecyclerAdapter.CommentHolder>() {

    val dateTimeFormatter: DateTimeFormatter by getKoin().inject()

    var data: List<Comment> = startData
        set(value) {
            GlobalScope.launch(Dispatchers.Main) {
                val commentsDiffResult = withContext(Dispatchers.Default) {
                    val diffUtilCallback =
                        CommentsDiffUtilCallback(
                            data,
                            value
                        )
                    DiffUtil.calculateDiff(diffUtilCallback)
                }
                field = value
                commentsDiffResult.dispatchUpdatesTo(this@CommentsRecyclerAdapter)
            }
        }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CommentHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_recycler_comment, parent, false)
        )

    override fun onBindViewHolder(holder: CommentHolder, position: Int) = holder.bind(position)

    inner class CommentHolder internal constructor(view: View): RecyclerView.ViewHolder(view) {
        private val avatarView: AvatarView = view.avatarView
        private val creatorNameTV: TextView = view.creatorNameTV
        private val commentTextTV: TextView = view.commentTextTV
        private val commentDateTV: TextView = view.commentDateTV

        fun bind(position: Int) {
            val creatorName = data[position].author.split(" ")
            this.avatarView.text = "${creatorName[0].first()}${creatorName[1].first()}"
            this.creatorNameTV.text = data[position].author
            this.commentTextTV.text = data[position].text
            this.commentDateTV.text = dateTimeFormatter.generateDateFromDateTimeForComment(data[position].commentTime)
        }
    }
}