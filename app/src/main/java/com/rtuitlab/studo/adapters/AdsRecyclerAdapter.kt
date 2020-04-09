package com.rtuitlab.studo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rtuitlab.studo.R
import com.rtuitlab.studo.server.general.ads.models.CompactAd
import kotlinx.android.synthetic.main.view_recycler_ad.view.*

class AdsRecyclerAdapter(
    private val data: List<CompactAd>
): RecyclerView.Adapter<AdsRecyclerAdapter.AdHolder>() {

    private var clickListener: OnAdClickListener? = null

    fun handleFavouriteError(compactAd: CompactAd) {
        data.forEach {
            if (it.id == compactAd.id) {
                it.isFavorite = compactAd.isFavorite
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
        private val favouriteIV: ImageView = view.favourite

        fun bind(position: Int) {
            nameTV.text = data[position].name
            descTV.text = data[position].shortDescription
            if (data[position].isFavorite) {
                favouriteIV.setImageResource(R.drawable.ic_star)
            } else {
                favouriteIV.setImageResource(R.drawable.ic_star_border)
            }
        }

        init {
            view.setOnClickListener {
                clickListener?.onAdClicked(data[adapterPosition])
            }
            favouriteIV.setOnClickListener {
                data[adapterPosition].isFavorite = !data[adapterPosition].isFavorite
                this.bind(adapterPosition)
                clickListener?.onFavouriteToggle(data[adapterPosition])
            }
        }
    }

    fun setOnAdClickListener(onAdClickListener: OnAdClickListener?) {
        clickListener = onAdClickListener
    }

    interface OnAdClickListener {
        fun onAdClicked(compactAd: CompactAd)
        fun onFavouriteToggle(compactAd: CompactAd)
    }
}