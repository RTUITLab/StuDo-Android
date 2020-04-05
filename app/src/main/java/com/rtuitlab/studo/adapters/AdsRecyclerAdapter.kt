package com.rtuitlab.studo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rtuitlab.studo.R
import com.rtuitlab.studo.server.general.ads.models.CompactAd
import kotlinx.android.synthetic.main.view_recycler_ad.view.*

class AdsRecyclerAdapter(
    private val data: List<CompactAd>
): RecyclerView.Adapter<AdsRecyclerAdapter.AdHolder>() {

    private var clickListener: OnAdClickListener? = null

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdHolder =
        AdHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_recycler_ad, parent, false)
        )

    override fun onBindViewHolder(holder: AdHolder, position: Int) = holder.bind(position)

    inner class AdHolder internal constructor(view: View):
        RecyclerView.ViewHolder(view), View.OnClickListener {
        private val nameTV: TextView = view.name
        private val descTV: TextView = view.desc

        fun bind(position: Int) {
            nameTV.text = data[position].name
            descTV.text = data[position].shortDescription
        }

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener?.onAdClicked(data[adapterPosition])
        }
    }

    fun setOnAdClickListener(onAdClickListener: OnAdClickListener?) {
        clickListener = onAdClickListener
    }

    interface OnAdClickListener {
        fun onAdClicked(compactAd: CompactAd)
    }
}