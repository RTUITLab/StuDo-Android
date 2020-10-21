package ru.rtuitlab.studo.ui.general

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.rtuitlab.studo.R
import ru.rtuitlab.studo.extensions.argument

class YesNoDialog: DialogFragment() {

    companion object {
        const val RESULT_YES_NO_KEY = "RESULT_YES_NO"

        fun newInstance(requestKey: String, title: String): YesNoDialog {
            return YesNoDialog().apply {
                this.requestKey = requestKey
                this.title = title
            }
        }
    }

    private var requestKey: String by argument()
    private var title: String by argument()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setNegativeButton(R.string.no) { _, _ ->
                setFragmentResult(
                    requestKey,
                    bundleOf(RESULT_YES_NO_KEY to false)
                )
            }
            .setPositiveButton(R.string.yes) { _, _ ->
                setFragmentResult(
                    requestKey,
                    bundleOf(RESULT_YES_NO_KEY to true)
                )
            }
            .create()
    }
}