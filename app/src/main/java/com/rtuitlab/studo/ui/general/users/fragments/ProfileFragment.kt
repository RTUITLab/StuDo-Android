package com.rtuitlab.studo.ui.general.users.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rtuitlab.studo.R
import com.rtuitlab.studo.databinding.FragmentProfileBinding
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.ui.general.ads.fragments.AdsListFragment.Companion.ADS_TYPE_KEY
import com.rtuitlab.studo.ui.general.resumes.ResumesListFragment.Companion.RESUMES_TYPE_KEY
import com.rtuitlab.studo.viewmodels.ads.FavouritesAds
import com.rtuitlab.studo.viewmodels.ads.OwnAds
import com.rtuitlab.studo.viewmodels.resumes.OwnResumes
import com.rtuitlab.studo.viewmodels.users.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.currentUserResource.value?.status != Status.SUCCESS) {
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
        profilePanel.setOnClickListener { navigateToProfile() }

        profileList.setOnMenuItemClickListener (
            onAds = { navigateToAds() },
            onResumes = { navigateToResumes() },
            onFavourites = { navigateToFavourites() },
            onOrganizations = {
                Snackbar.make(requireView(), "WORK IN PROGRESS", Snackbar.LENGTH_SHORT).show()
            },
            onSettings = { navigateToSettings() },
            onAbout = {
                Snackbar.make(requireView(), "ABOUT", Snackbar.LENGTH_SHORT).show()
            }
        )
    }

    private fun navigateToProfile() {
        val extras = FragmentNavigatorExtras(
            avatarView to "avatarView"
        )
        findNavController().navigate(
            R.id.action_profileFragment_to_accountSettingsFragment, null, null, extras
        )
    }

    private fun navigateToAds() {
        val bundle = bundleOf(ADS_TYPE_KEY to OwnAds)
        findNavController().navigate(R.id.action_profileFragment_to_ads, bundle)
    }

    private fun navigateToResumes() {
        val bundle = bundleOf(RESUMES_TYPE_KEY to OwnResumes)
        findNavController().navigate(R.id.action_profileFragment_to_resumes, bundle)
    }

    private fun navigateToFavourites() {
        val bundle = bundleOf(ADS_TYPE_KEY to FavouritesAds)
        findNavController().navigate(R.id.action_profileFragment_to_ads, bundle)
    }

    private fun navigateToSettings() {
        findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
    }
}
