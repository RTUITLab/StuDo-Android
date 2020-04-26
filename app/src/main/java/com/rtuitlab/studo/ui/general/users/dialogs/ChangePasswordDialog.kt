package com.rtuitlab.studo.ui.general.users.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rtuitlab.studo.R
import com.rtuitlab.studo.databinding.DialogChangePasswordBinding
import com.rtuitlab.studo.viewmodels.users.AccountChangesDialogsViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChangePasswordDialog: DialogFragment() {

    private val viewModel: AccountChangesDialogsViewModel by sharedViewModel()

    private lateinit var positiveBtn: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogChangePasswordBinding.inflate(
            requireActivity().layoutInflater,
            null,
            false
        )
        binding.viewModel = viewModel
        binding.dialog = this
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.change_password)
            .setView(binding.root)
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)
            .create()
            .apply {
                setOnShowListener {
                    positiveBtn = getButton(AlertDialog.BUTTON_POSITIVE)
                    positiveBtn.isEnabled = false
                    positiveBtn.setOnClickListener {
                        viewModel.changePassword()
                        currentFocus?.let {
                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                            imm?.hideSoftInputFromWindow(it.windowToken, 0)
                        }
                        dismiss()
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!requireActivity().isChangingConfigurations) {
            viewModel.clearData()
        }
    }

    fun checkData() {
        positiveBtn.isEnabled = viewModel.isDataValid(AccountChangesDialogsViewModel.DataType.PASSWORD)
    }
}