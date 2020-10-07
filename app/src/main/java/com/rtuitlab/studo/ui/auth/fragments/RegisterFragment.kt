package com.rtuitlab.studo.ui.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.rtuitlab.studo.R
import com.rtuitlab.studo.databinding.FragmentRegisterBinding
import com.rtuitlab.studo.extensions.hideProgress
import com.rtuitlab.studo.extensions.longSnackbar
import com.rtuitlab.studo.extensions.shortToast
import com.rtuitlab.studo.extensions.showProgress
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.viewmodels.auth.AuthViewModel
import kotlinx.android.synthetic.main.fragment_register.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RegisterFragment : Fragment() {

    private val viewModel: AuthViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentRegisterBinding>(
            inflater,
            R.layout.
            fragment_register,
            container,
            false
        )
        binding.viewModel = viewModel
        bindProgressButton(binding.registerBtn)
        binding.registerBtn.attachTextChangeAnimator()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!requireActivity().isChangingConfigurations) {
            viewModel.clearErrors()
        }
    }

    private fun setListeners() {
        loginLink.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setObservers() {
        viewModel.registerResource.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    requireActivity().window.decorView.rootView
                        .longSnackbar(getString(R.string.email_verification))
                        .show()
                    loginLink.performClick()
                }
                Status.ERROR -> {
                    registerBtn.hideProgress(R.string.create_account)
                    requireContext().shortToast(it.message).show()
                }
                Status.LOADING -> registerBtn.showProgress()
            }
        })
    }
}
