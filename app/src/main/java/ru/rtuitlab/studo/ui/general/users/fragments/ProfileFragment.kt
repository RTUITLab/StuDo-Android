package ru.rtuitlab.studo.ui.general.users.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.rtuitlab.studo.R
import ru.rtuitlab.studo.databinding.FragmentProfileBinding
import ru.rtuitlab.studo.extensions.shortSnackbar
import ru.rtuitlab.studo.extensions.shortToast
import ru.rtuitlab.studo.server.Status
import ru.rtuitlab.studo.ui.general.ads.fragments.AdsListFragment.Companion.ADS_TYPE_KEY
import ru.rtuitlab.studo.ui.general.resumes.ResumesListFragment.Companion.RESUMES_TYPE_KEY
import ru.rtuitlab.studo.viewmodels.ads.FavouritesAds
import ru.rtuitlab.studo.viewmodels.ads.OwnAds
import ru.rtuitlab.studo.viewmodels.resumes.OwnResumes
import ru.rtuitlab.studo.viewmodels.users.ProfileViewModel

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
        viewModel.currentUserResource.observe(viewLifecycleOwner, {
            if (it.status == Status.ERROR) {
                requireContext().shortToast(it.message).show()
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
                requireView().shortSnackbar("WORK IN PROGRESS").show()
            },
            onSettings = { navigateToSettings() },
            onAbout = { navigateToAbout() }
        )
    }

    private fun navigateToProfile() = findNavController().navigate(
        R.id.action_profileFragment_to_accountSettingsFragment,
        null,
        null,
        FragmentNavigatorExtras(avatarView to "avatarView")
    )

    private fun navigateToAds() = findNavController().navigate(
        R.id.action_profileFragment_to_ads,
        bundleOf(ADS_TYPE_KEY to OwnAds)
    )

    private fun navigateToResumes() = findNavController().navigate(
        R.id.action_profileFragment_to_resumes,
        bundleOf(RESUMES_TYPE_KEY to OwnResumes)
    )

    private fun navigateToFavourites() = findNavController().navigate(
        R.id.action_profileFragment_to_ads,
        bundleOf(ADS_TYPE_KEY to FavouritesAds)
    )

    private fun navigateToSettings() = findNavController().navigate(
        R.id.action_profileFragment_to_settingsFragment
    )

    private fun navigateToAbout() = findNavController().navigate(
        R.id.action_profileFragment_to_aboutFragment
    )
}
