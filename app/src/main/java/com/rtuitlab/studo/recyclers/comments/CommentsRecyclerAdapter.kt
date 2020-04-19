package com.rtuitlab.studo.recyclers.comments

import android.view.*
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rtuitlab.studo.utils.DateTimeFormatter
import com.rtuitlab.studo.R
import com.rtuitlab.studo.account.AccountStorage
import com.rtuitlab.studo.custom_views.AvatarView
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

    private val accStorage: AccountStorage by getKoin().inject()

    private var clickListener: OnCommentClickListener? = null

    val dateTimeFormatter: DateTimeFormatter by getKoin().inject()

    private var data: List<Comment> = startData

    fun updateData(newData: List<Comment>, onComplete: (() -> Unit)? = null) {
        GlobalScope.launch(Dispatchers.Main) {
            val commentsDiffResult = withContext(Dispatchers.Default) {
                val diffUtilCallback =
                    CommentsDiffUtilCallback(
                        data,
                        newData
                    )
                DiffUtil.calculateDiff(diffUtilCallback)
            }
            data = newData
            commentsDiffResult.dispatchUpdatesTo(this@CommentsRecyclerAdapter)
            onComplete?.invoke()
        }
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CommentHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_recycler_comment, parent, false)
        )

    override fun onBindViewHolder(holder: CommentHolder, position: Int) = holder.bind(position)

    inner class CommentHolder internal constructor(view: View):
        RecyclerView.ViewHolder(view), PopupMenu.OnMenuItemClickListener {
        private val avatarView: AvatarView = view.avatarView
        private val creatorNameTV: TextView = view.creatorNameTV
        private val commentTextTV: TextView = view.commentTextTV
        private val commentDateTV: TextView = view.commentDateTV

        init {
            view.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    if (data[adapterPosition].authorId == accStorage.user.id) {
                        inflate(R.menu.own_comment)
                    } else {
                        inflate(R.menu.comment)
                    }
                    setOnMenuItemClickListener(this@CommentHolder)
                }.show()
            }
        }

        fun bind(position: Int) {
            val creatorName = data[position].author.split(" ")
            this.avatarView.text = "${creatorName[0].first()}${creatorName[1].first()}"
            this.creatorNameTV.text = data[position].author
            this.commentTextTV.text = data[position].text
            this.commentDateTV.text = dateTimeFormatter.generateDateFromDateTimeForComment(data[position].commentTime)
        }

        override fun onMenuItemClick(item: MenuItem?): Boolean {
            return when(item?.itemId) {
                R.id.action_profile -> {
                    clickListener?.onNavigateToProfile(data[adapterPosition])
                    true
                }
                R.id.action_delete -> {
                    clickListener?.onDeleteComment(data[adapterPosition])
                    true
                }
                else -> false
            }
        }
    }

    fun setOnCommentClickListener(onCommentClickListener: OnCommentClickListener) {
        clickListener = onCommentClickListener
    }

    interface OnCommentClickListener {
        fun onNavigateToProfile(comment: Comment)
        fun onDeleteComment(comment: Comment)
    }
}