package com.rtuitlab.studo.ui.general.users.fragments

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
import com.google.android.material.snackbar.Snackbar
import com.rtuitlab.studo.R
import com.rtuitlab.studo.databinding.FragmentProfileBinding
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.viewmodels.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.currentUserResource.value == null) {
            viewModel.updateCurrentUser()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentProfileBinding>(
            inflater,
            R.layout.fragment_profile,
            container,
            false
        )
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collapsingToolbar.title = getString(R.string.title_profile)
        setMenuListener()
        viewModel.currentUserResource.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        })
    }

    private fun setMenuListener() {
        profilePanel.setOnClickListener {
            val extras = FragmentNavigatorExtras(
                avatarView to "avatarView"
            )
            findNavController().navigate(
                R.id.action_profileFragment_to_accountSettingsFragment, null, null, extras
            )
        }
        profileList.setOnMenuItemClickListener (
            onAds = {
                val bundle = Bundle().apply {
                    putSerializable(AdsType::class.java.simpleName, MyAds)
                }
                findNavController().navigate(R.id.action_profileFragment_to_adsListFragment2, bundle)
            },
            onResumes = {
                val bundle = Bundle().apply {
                    putSerializable(ResumesType::class.java.simpleName, MyResumes)
                }
                findNavController().navigate(R.id.action_profileFragment_to_resumesListFragment2, bundle)
            },
            onBookmarks = {
                val bundle = Bundle().apply {
                    putSerializable(AdsType::class.java.simpleName, BookmarkedAds)
                }
                findNavController().navigate(R.id.action_profileFragment_to_adsListFragment2, bundle)
            },
            onOrganizations = {
                Snackbar.make(requireView(), "WORK IN PROGRESS", Snackbar.LENGTH_SHORT).show()
            },
            onSettings = {
                findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
            },
            onAbout = {
                Snackbar.make(requireView(), "ABOUT", Snackbar.LENGTH_SHORT).show()
            }
        )
    }
}
