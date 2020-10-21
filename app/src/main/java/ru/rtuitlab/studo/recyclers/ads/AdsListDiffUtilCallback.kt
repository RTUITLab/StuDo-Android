package ru.rtuitlab.studo.recyclers.ads

import androidx.recyclerview.widget.DiffUtil
import ru.rtuitlab.studo.server.general.ads.models.CompactAd

class AdsListDiffUtilCallback (
    private val oldList: List<CompactAd>,
    private val newList: List<CompactAd>
): DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldAd = oldList[oldItemPosition]
        val newAd = newList[newItemPosition]
        return oldAd.id == newAd.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldAd = oldList[oldItemPosition]
        val newAd = newList[newItemPosition]
        return oldAd.name == newAd.name &&
                oldAd.shortDescription == newAd.shortDescription &&
                oldAd.isFavourite == newAd.isFavourite
    }

}