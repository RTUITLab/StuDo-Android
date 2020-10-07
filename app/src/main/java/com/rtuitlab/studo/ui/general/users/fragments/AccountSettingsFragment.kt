package com.rtuitlab.studo.ui.general.users.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialContainerTransform
import com.rtuitlab.studo.R
import com.rtuitlab.studo.databinding.FragmentAccountSettingsBinding
import com.rtuitlab.studo.extensions.mainActivity
import com.rtuitlab.studo.extensions.shortSnackbar
import com.rtuitlab.studo.extensions.shortToast
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.ui.general.MainActivity
import com.rtuitlab.studo.viewmodels.users.AccountChangesDialogsViewModel
import com.rtuitlab.studo.viewmodels.users.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_account_settings.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class AccountSettingsFragment: Fragment() {

    private val profileViewModel: ProfileViewModel by sharedViewModel()
    private val dialogsViewModel: AccountChangesDialogsViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentAccountSettingsBinding>(
            inflater,
            R.layout.fragment_account_settings,
            container,
            false
        )
        binding.viewModel = profileViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collapsingToolbar.title = getString(R.string.account)
        mainActivity().enableNavigateButton(collapsingToolbar.toolbar)

        setListeners()
        setObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!requireActivity().isChangingConfigurations) {
            profileViewModel.clearChanges()
        }
    }

    private fun setListeners() {
        changeEmailBtn.setOnClickListener {
            findNavController().navigate(R.id.action_accountSettingsFragment_to_changeEmailDialog)
        }

        changePasswordBtn.setOnClickListener {
            findNavController().navigate(R.id.action_accountSettingsFragment_to_changePasswordDialog)
        }

        logoutBtn.setOnClickListener {
            profileViewModel.logout()
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun setObservers() {
        profileViewModel.currentUserResource.observe(viewLifecycleOwner, {
            when(it.status) {
                Status.SUCCESS -> swipeContainer.isRefreshing = false
                Status.ERROR -> {
                    swipeContainer.isRefreshing = false
                    requireContext().shortToast(it.message).show()
                }
                Status.LOADING -> swipeContainer.isRefreshing = true
            }
        })

        profileViewModel.changesSavedResource.observe(viewLifecycleOwner, {
            when(it.status) {
                Status.SUCCESS -> {
                    swipeContainer.isRefreshing = false
                    requireView().shortSnackbar(getString(R.string.changes_saved)).show()
                }
                Status.ERROR -> {
                    swipeContainer.isRefreshing = false
                    requireContext().shortToast(it.message).show()
                }
                Status.LOADING -> swipeContainer.isRefreshing = true
            }
        })

        dialogsViewModel.changeEmailResource.observe(viewLifecycleOwner, {
            when(it.status) {
                Status.SUCCESS -> {
                    swipeContainer.isRefreshing = false
                    requireView().shortSnackbar(getString(R.string.new_email_verification)).show()
                }
                Status.ERROR -> {
                    swipeContainer.isRefreshing = false
                    requireContext().shortToast(it.message).show()
                }
                Status.LOADING -> swipeContainer.isRefreshing = true
            }
        })

        dialogsViewModel.changePasswordResource.observe(viewLifecycleOwner, {
            when(it.status) {
                Status.SUCCESS -> {
                    swipeContainer.isRefreshing = false
                    requireView().shortSnackbar(getString(R.string.password_changed)).show()
                }
                Status.ERROR -> {
                    swipeContainer.isRefreshing = false
                    requireContext().shortToast(it.message).show()
                }
                Status.LOADING -> swipeContainer.isRefreshing = true
            }
        })
    }
}