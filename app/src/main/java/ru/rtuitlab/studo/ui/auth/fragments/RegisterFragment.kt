package ru.rtuitlab.studo.ui.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import kotlinx.android.synthetic.main.fragment_register.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.rtuitlab.studo.R
import ru.rtuitlab.studo.databinding.FragmentRegisterBinding
import ru.rtuitlab.studo.extensions.hideProgress
import ru.rtuitlab.studo.extensions.longSnackbar
import ru.rtuitlab.studo.extensions.shortToast
import ru.rtuitlab.studo.extensions.showProgress
import ru.rtuitlab.studo.server.Status
import ru.rtuitlab.studo.viewmodels.auth.AuthViewModel

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
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun setObservers() {
        viewModel.registerResource.observe(viewLifecycleOwner, {
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
