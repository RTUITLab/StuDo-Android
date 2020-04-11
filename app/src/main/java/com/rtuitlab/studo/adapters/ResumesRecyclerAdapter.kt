package com.rtuitlab.studo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rtuitlab.studo.R
import com.rtuitlab.studo.server.general.resumes.models.CompactResume
import kotlinx.android.synthetic.main.view_recycler_resume.view.*

class ResumesRecyclerAdapter(
    private val data: List<CompactResume>
): RecyclerView.Adapter<ResumesRecyclerAdapter.ResumeHolder>() {

    private var clickListener: OnResumeClickListener? = null

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ResumeHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_recycler_resume, parent, false)
        )

    override fun onBindViewHolder(holder: ResumeHolder, position: Int) = holder.bind(position)

    inner class ResumeHolder internal constructor(view: View):
        RecyclerView.ViewHolder(view) {
        private val name: TextView = view.title
        private val description: TextView = view.desc

        fun bind(position: Int) {
            this.name.text = data[position].name
            this.description.text = data[position].description
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