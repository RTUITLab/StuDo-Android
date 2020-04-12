package com.rtuitlab.studo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rtuitlab.studo.diff_util.AdsListDiffUtilCallback
import com.rtuitlab.studo.R
import com.rtuitlab.studo.server.general.ads.models.AdIdWithIsFavourite
import com.rtuitlab.studo.server.general.ads.models.CompactAd
import kotlinx.android.synthetic.main.view_recycler_ad.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdsRecyclerAdapter: RecyclerView.Adapter<AdsRecyclerAdapter.AdHolder>() {

    private var clickListener: OnAdClickListener? = null

    var data: List<CompactAd> = listOf()
    set(value) {
        GlobalScope.launch(Dispatchers.Main) {
            val adsDiffResult = withContext(Dispatchers.Default) {
                val diffUtilCallback =
                    AdsListDiffUtilCallback(
                        data,
                        value
                    )
                DiffUtil.calculateDiff(diffUtilCallback)
            }
            field = value
            adsDiffResult.dispatchUpdatesTo(this@AdsRecyclerAdapter)
        }
    }

    fun handleFavouriteError(adIdWithIsFavourite: AdIdWithIsFavourite) {
        data.forEach {
            if (it.id == adIdWithIsFavourite.id) {
                it.isFavourite = adIdWithIsFavourite.isFavourite
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

    override fun onBindViewHolder(holder: AdHolder, position: Int) = holder.bind(data[position])

    inner class AdHolder internal constructor(view: View): RecyclerView.ViewHolder(view){
        private val nameTV: TextView = view.title
        private val descTV: TextView = view.desc
        private val favouriteIV: ImageView = view.favouriteBtn

        fun bind(compactAd: CompactAd) {
            nameTV.text = compactAd.name
            descTV.text = compactAd.shortDescription
            if (compactAd.isFavourite) {
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
                val compactAd = data[adapterPosition]
                compactAd.isFavourite = !compactAd.isFavourite
                this.bind(compactAd)
                clickListener?.onFavouriteToggle(compactAd)
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