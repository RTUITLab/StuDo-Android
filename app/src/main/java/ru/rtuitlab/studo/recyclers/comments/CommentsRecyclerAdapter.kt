package ru.rtuitlab.studo.recyclers.comments

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_recycler_comment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.getKoin
import ru.rtuitlab.studo.R
import ru.rtuitlab.studo.account.AccountStorage
import ru.rtuitlab.studo.custom_views.AvatarView
import ru.rtuitlab.studo.server.general.ads.models.Comment
import ru.rtuitlab.studo.utils.DateTimeFormatter

class CommentsRecyclerAdapter(
    startData: List<Comment>
): RecyclerView.Adapter<CommentsRecyclerAdapter.CommentHolder>() {

    private val accStorage: AccountStorage by getKoin().inject()

    private val dateTimeFormatter: DateTimeFormatter by getKoin().inject()

    private var clickListener: OnCommentClickListener? = null

    private var data: List<Comment>  = startData

    fun updateData(newData: List<Comment>, onComplete: (() -> Unit)? = null) {
        GlobalScope.launch(Dispatchers.Main) {
            val commentsDiffResult = withContext(Dispatchers.Default) {
                DiffUtil.calculateDiff(CommentsDiffUtilCallback(data, newData))
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
        RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
        private val avatarView: AvatarView = view.avatarView
        private val creatorNameTV: TextView = view.creatorNameTV
        private val commentTextTV: TextView = view.commentTextTV
        private val commentDateTV: TextView = view.commentDateTV

        init {
            view.setOnCreateContextMenuListener(this)
        }

        fun bind(position: Int) {
            val creatorName = data[position].author.split(" ")
            this.avatarView.text = "${creatorName[0].first()}${creatorName[1].first()}"
            this.creatorNameTV.text = data[position].author
            this.commentTextTV.text = data[position].text
            this.commentDateTV.text = dateTimeFormatter.generateDateFromDateTimeForComment(data[position].commentTime)
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            val clickedComment = data[bindingAdapterPosition]

            menu?.add(Menu.NONE, Menu.NONE, Menu.NONE, v?.context?.getString(R.string.user_profile))?.apply {
                setOnMenuItemClickListener {
                    clickListener?.onNavigateToProfile(clickedComment)
                    true
                }
            }

            if (clickedComment.authorId == accStorage.user.id) {
                menu?.add(Menu.NONE, Menu.NONE, Menu.NONE, v?.context?.getString(R.string.delete))?.apply {
                    setOnMenuItemClickListener {
                        clickListener?.onDeleteComment(clickedComment)
                        true
                    }
                }
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