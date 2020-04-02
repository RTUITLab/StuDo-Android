package com.rtuitlab.studo.ui.auth.fragments

import android.os.Bundle
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.ChangeBounds
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.Snackbar
import com.rtuitlab.studo.R
import com.rtuitlab.studo.databinding.FragmentRegisterBinding
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.viewmodels.AuthViewModel
import kotlinx.android.synthetic.main.fragment_register.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RegisterFragment : Fragment() {

    private val viewModel: AuthViewModel by sharedViewModel()

    private val registerObserver = Observer<Resource<Unit>> {
        registerBtn.revertAnimation()
        when(it.status) {
            Status.SUCCESS -> {
                Snackbar.make(requireView(), getString(R.string.email_verification), Snackbar.LENGTH_LONG).show()
                loginLink.performClick()
            }
            Status.ERROR -> {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
            Status.LOADING -> registerBtn.startAnimation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        viewModel.registerResource.observe(viewLifecycleOwner, registerObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearErrors()
        registerBtn.dispose()
    }

    private fun setListeners() {
        loginLink.setOnClickListener {
            viewModel.clearErrors()
            val extras = FragmentNavigatorExtras(
                logoView to "loginLogoView"
            )
            findNavController().navigate(
                R.id.action_registerFragment_to_loginFragment, null, null, extras
            )
        }
    }
}
