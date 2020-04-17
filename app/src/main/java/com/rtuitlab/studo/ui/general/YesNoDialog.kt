package com.rtuitlab.studo.ui.general

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rtuitlab.studo.R
import java.io.Serializable

class YesNoDialog: DialogFragment() {

    private val listenerKey = "listener"
    private val titleKey = "title"

    lateinit var title: String
    lateinit var listener: OnYesClickListener

    companion object {
        fun getInstance(title: String, onAccept: OnYesClickListener): YesNoDialog {
            val dialog = YesNoDialog()
            dialog.title = title
            dialog.listener = onAccept
            return dialog
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(listenerKey, listener)
        outState.putString(titleKey, title)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        savedInstanceState?.let {
            title = it.getString(titleKey)!!
            listener = it.getSerializable(listenerKey) as OnYesClickListener
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes) { _, _ ->
                listener.onYesClicked()
            }
            .create()
    }

    interface OnYesClickListener: Serializable {
        fun onYesClicked()
    }
}