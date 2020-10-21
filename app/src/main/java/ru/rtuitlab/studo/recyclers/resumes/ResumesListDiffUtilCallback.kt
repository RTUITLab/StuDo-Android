package ru.rtuitlab.studo.recyclers.resumes

import androidx.recyclerview.widget.DiffUtil
import ru.rtuitlab.studo.server.general.resumes.models.CompactResume

class ResumesListDiffUtilCallback (
    private val oldList: List<CompactResume>,
    private val newList: List<CompactResume>
): DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldResume = oldList[oldItemPosition]
        val newResume = newList[newItemPosition]
        return oldResume.id == newResume.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldResume = oldList[oldItemPosition]
        val newResume = newList[newItemPosition]
        return oldResume.name == newResume.name &&
                oldResume.description == newResume.description
    }

}