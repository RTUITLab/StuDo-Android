package ru.rtuitlab.studo.ui.auth.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.rtuitlab.studo.R
import ru.rtuitlab.studo.databinding.DialogResetPasswordBinding
import ru.rtuitlab.studo.viewmodels.auth.AuthViewModel

class ResetPasswordDialog: DialogFragment() {

    private val viewModel: AuthViewModel by sharedViewModel()

    private lateinit var positiveBtn: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogResetPasswordBinding.inflate(
            requireActivity().layoutInflater,
            null,
            false
        )
        binding.viewModel = viewModel
        binding.dialog = this
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.reset_password)
            .setView(binding.root)
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)
            .create()
            .apply {
                setOnShowListener {
                    positiveBtn = getButton(AlertDialog.BUTTON_POSITIVE)
                    positiveBtn.isEnabled = false
                    positiveBtn.setOnClickListener {
                        viewModel.resetPassword()
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
            viewModel.clearResetEmail()
        }
    }

    fun checkData() {
        positiveBtn.isEnabled = viewModel.isDataValid()
    }
}