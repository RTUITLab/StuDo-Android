package ru.rtuitlab.studo.recyclers.resumes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_recycler_resume.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rtuitlab.studo.R
import ru.rtuitlab.studo.custom_views.AvatarView
import ru.rtuitlab.studo.server.general.resumes.models.CompactResume

class ResumesRecyclerAdapter: RecyclerView.Adapter<ResumesRecyclerAdapter.ResumeHolder>() {

    private var clickListener: OnResumeClickListener? = null

    var data: List<CompactResume> = listOf()
    set(value) {
        GlobalScope.launch(Dispatchers.Main) {
            val resumesDiffResult = withContext(Dispatchers.Default) {
                DiffUtil.calculateDiff(ResumesListDiffUtilCallback(data, value))
            }
            field = value
            resumesDiffResult.dispatchUpdatesTo(this@ResumesRecyclerAdapter)
        }
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ResumeHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_recycler_resume, parent, false)
        )

    override fun onBindViewHolder(holder: ResumeHolder, position: Int) = holder.bind(data[position])

    inner class ResumeHolder internal constructor(view: View):
        RecyclerView.ViewHolder(view) {
        private val name: TextView = view.title
        private val description: TextView = view.desc
        private val avatarView: AvatarView = view.avatarView

        init {
            view.setOnClickListener {
                clickListener?.onResumeClick(data[bindingAdapterPosition])
            }
        }

        fun bind(compactResume: CompactResume) {
            this.name.text = compactResume.name
            this.description.text = compactResume.description
            val userName = compactResume.userName.split(" ")
            this.avatarView.text = "${userName[0].first()}${userName[1].first()}"
        }
    }

    fun setOnResumeClickListener(onResumeClickListener: OnResumeClickListener) {
        clickListener = onResumeClickListener
    }

    interface OnResumeClickListener {
        fun onResumeClick(compactResume: CompactResume)
    }
}