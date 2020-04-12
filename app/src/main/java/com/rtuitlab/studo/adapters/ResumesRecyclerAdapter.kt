package com.rtuitlab.studo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rtuitlab.studo.R
import com.rtuitlab.studo.diff_util.ResumesListDiffUtilCallback
import com.rtuitlab.studo.server.general.resumes.models.CompactResume
import kotlinx.android.synthetic.main.view_recycler_resume.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResumesRecyclerAdapter: RecyclerView.Adapter<ResumesRecyclerAdapter.ResumeHolder>() {

    private var clickListener: OnResumeClickListener? = null

    var data: List<CompactResume> = listOf()
    set(value) {
        GlobalScope.launch(Dispatchers.Main) {
            val resumesDiffResult = withContext(Dispatchers.Default) {
                val diffUtilCallback =
                    ResumesListDiffUtilCallback(
                        data,
                        value
                    )
                DiffUtil.calculateDiff(diffUtilCallback)
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

        fun bind(compactResume: CompactResume) {
            this.name.text = compactResume.name
            this.description.text = compactResume.description
        }

        init {
            view.setOnClickListener {
                clickListener?.onResumeClick(data[adapterPosition])
            }
        }
    }

    fun setOnResumeClickListener(onResumeClickListener: OnResumeClickListener) {
        clickListener = onResumeClickListener
    }

    interface OnResumeClickListener {
        fun onResumeClick(compactResume: CompactResume)
    }
}