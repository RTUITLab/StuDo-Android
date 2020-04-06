package com.rtuitlab.studo.ui.general.profile.fragments

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import com.rtuitlab.studo.R
import com.rtuitlab.studo.currentUser
import com.rtuitlab.studo.databinding.FragmentAccountSettingsBinding
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.ui.general.MainActivity
import com.rtuitlab.studo.viewmodels.AccountChangesDialogsViewModel
import com.rtuitlab.studo.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_account_settings.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class AccountSettingsFragment: Fragment() {

    private val profileViewModel: ProfileViewModel by sharedViewModel()
    private val dialogsViewModel: AccountChangesDialogsViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis.create(requireContext(), MaterialSharedAxis.Z,true)
        exitTransition = MaterialSharedAxis.create(requireContext(), MaterialSharedAxis.Z,false)
        sharedElementEnterTransition = MaterialContainerTransform(requireContext()).apply {
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
        setListeners()
        setObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        profileViewModel.clearChanges()
    }

    private fun setListeners() {
        changeEmailBtn.setOnClickListener {
            dialogsViewModel.clearData()
            findNavController().navigate(R.id.action_accountSettingsFragment_to_changeEmailDialog)
        }

        logoutBtn.setOnClickListener {
            currentUser = null
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            requireActivity().startActivity(intent)
            requireActivity().finish()
            Runtime.getRuntime().exit(0)
        }
    }

    private fun setObservers() {
        profileViewModel.currentUserResource.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    swipeContainer.isRefreshing = false
                }
                Status.ERROR -> {
                    swipeContainer.isRefreshing = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {
                    swipeContainer.isRefreshing = true
                }
            }
        })

        profileViewModel.changesSavedResource.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    swipeContainer.isRefreshing = false
                    Snackbar.make(requireView(), getString(R.string.changes_saved), Snackbar.LENGTH_SHORT).show()
                }
                Status.ERROR -> {
                    swipeContainer.isRefreshing = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {
                    swipeContainer.isRefreshing = true
                }
            }
        })

        dialogsViewModel.changeEmailResource.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    swipeContainer.isRefreshing = false
                    Snackbar.make(requireView(), getString(R.string.new_email_verification), Snackbar.LENGTH_SHORT).show()
                }
                Status.ERROR -> {
                    swipeContainer.isRefreshing = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {
                    swipeContainer.isRefreshing = true
                }
            }
        })
    }
}