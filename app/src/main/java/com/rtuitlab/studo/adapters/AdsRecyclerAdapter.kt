package com.rtuitlab.studo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rtuitlab.studo.R
import com.rtuitlab.studo.server.general.ads.models.CompactAdWithBookmark
import kotlinx.android.synthetic.main.view_recycler_ad.view.*

class AdsRecyclerAdapter(
    private val data: List<CompactAdWithBookmark>
): RecyclerView.Adapter<AdsRecyclerAdapter.AdHolder>() {

    private var clickListener: OnAdClickListener? = null

    fun handleBookmarkError(compactAdWithBookmark: CompactAdWithBookmark) {
        data.forEach {
            if (it.ad.id == compactAdWithBookmark.ad.id) {
                it.isBookmarked = compactAdWithBookmark.isBookmarked
                notifyItemChanged(data.indexOf(it))
                return@forEach
            }
        }
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdHolder =
        AdHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_recycler_ad, parent, false)
        )

    override fun onBindViewHolder(holder: AdHolder, position: Int) = holder.bind(position)

    inner class AdHolder internal constructor(view: View): RecyclerView.ViewHolder(view){
        private val nameTV: TextView = view.title
        private val descTV: TextView = view.desc
        private val bookmarkIV: ImageView = view.bookmark

        fun bind(position: Int) {
            nameTV.text = data[position].ad.name
            descTV.text = data[position].ad.shortDescription
            if (data[position].isBookmarked) {
                bookmarkIV.setImageResource(R.drawable.ic_star)
            } else {
                bookmarkIV.setImageResource(R.drawable.ic_star_border)
            }
        }

        init {
            view.setOnClickListener {
                clickListener?.onAdClicked(data[adapterPosition])
            }
            bookmarkIV.setOnClickListener {
                data[adapterPosition].isBookmarked = !data[adapterPosition].isBookmarked
//                notifyItemChanged(adapterPosition)
                this.bind(adapterPosition)
                clickListener?.onBookmarkToggle(data[adapterPosition])
            }
        }
    }

    fun setOnAdClickListener(onAdClickListener: OnAdClickListener?) {
        clickListener = onAdClickListener
    }

    interface OnAdClickListener {
        fun onAdClicked(compactAdWithBookmark: CompactAdWithBookmark)
        fun onBookmarkToggle(compactAdWithBookmark: CompactAdWithBookmark)
    }
}