package com.rtuitlab.studo.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.rtuitlab.studo.R
import com.rtuitlab.studo.databinding.FragmentLoginBinding
import com.rtuitlab.studo.extensions.hideProgress
import com.rtuitlab.studo.extensions.longSnackbar
import com.rtuitlab.studo.extensions.shortToast
import com.rtuitlab.studo.extensions.showProgress
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.ui.general.MainActivity
import com.rtuitlab.studo.viewmodels.auth.AuthViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginFragment : Fragment() {

    private val viewModel: AuthViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(
            inflater,
            R.layout.
            fragment_login,
            container,
            false
        )
        binding.viewModel = viewModel
        bindProgressButton(binding.loginBtn)
        binding.loginBtn.attachTextChangeAnimator()
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
        registerLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        resetPasswordBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_resetPasswordDialog)
        }
    }

    private fun setObservers() {
        viewModel.loginResource.observe(viewLifecycleOwner, {
            when(it.status) {
                Status.SUCCESS -> {
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                }
                Status.ERROR -> {
                    loginBtn.hideProgress(R.string.login)
                    requireContext().shortToast(it.message).show()
                }
                Status.LOADING -> loginBtn.showProgress()
            }
        })

        viewModel.resetResource.observe(viewLifecycleOwner, {
            when(it.status) {
                Status.SUCCESS -> {
                    loginBtn.hideProgress(R.string.login)
                    requireView().longSnackbar(getString(R.string.check_email_to_reset)).show()
                }
                Status.ERROR -> {
                    loginBtn.hideProgress(R.string.login)
                    requireContext().shortToast(it.message).show()
                }
                Status.LOADING -> loginBtn.showProgress()
            }
        })
    }
}
