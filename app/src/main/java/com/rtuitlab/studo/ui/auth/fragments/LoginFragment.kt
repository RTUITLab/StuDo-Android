package com.rtuitlab.studo.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.rtuitlab.studo.R
import com.rtuitlab.studo.databinding.FragmentLoginBinding
import com.rtuitlab.studo.server.Resource
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.auth.models.UserLoginResponse
import com.rtuitlab.studo.ui.main.MainActivity
import com.rtuitlab.studo.viewmodels.AuthViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginFragment : Fragment() {

    private val viewModel: AuthViewModel by sharedViewModel()

    private val loginObserver = Observer<Resource<UserLoginResponse>> {
        loginBtn.revertAnimation()
        when(it.status) {
            Status.SUCCESS -> {
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }
            Status.ERROR -> {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
            Status.LOADING -> loginBtn.startAnimation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        viewModel.loginResource.observe(viewLifecycleOwner, loginObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearErrors()
        loginBtn.dispose()
    }

    private fun setListeners() {
        registerLink.setOnClickListener {
            viewModel.clearErrors()
            val extras = FragmentNavigatorExtras(
                logoView to "registerLogoView"
            )
            findNavController().navigate(
                R.id.action_loginFragment_to_registerFragment, null, null, extras
            )
        }
    }
}
