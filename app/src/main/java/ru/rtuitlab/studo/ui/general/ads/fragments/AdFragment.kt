package ru.rtuitlab.studo.ui.general.ads.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_ad.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.rtuitlab.studo.R
import ru.rtuitlab.studo.databinding.FragmentAdBinding
import ru.rtuitlab.studo.extensions.mainActivity
import ru.rtuitlab.studo.extensions.shortSnackbar
import ru.rtuitlab.studo.extensions.shortToast
import ru.rtuitlab.studo.server.Status
import ru.rtuitlab.studo.server.general.ads.models.Ad
import ru.rtuitlab.studo.server.general.ads.models.CompactAd
import ru.rtuitlab.studo.ui.general.YesNoDialog
import ru.rtuitlab.studo.ui.general.YesNoDialog.Companion.RESULT_YES_NO_KEY
import ru.rtuitlab.studo.ui.general.ads.fragments.CreateEditAdFragment.Companion.MODIFY_AD_TYPE_KEY
import ru.rtuitlab.studo.viewmodels.ads.AdViewModel
import ru.rtuitlab.studo.viewmodels.ads.AdsListViewModel
import ru.rtuitlab.studo.viewmodels.ads.EditAd

class AdFragment: Fragment() {

    companion object {
        private const val DELETE_AD_RESULT_REQUEST_KEY = "DELETE_AD_RESULT_REQUEST"
    }

    private val adsListViewModel: AdsListViewModel by viewModel()
    private val adViewModel: AdViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentAdBinding>(
            inflater,
            R.layout.fragment_ad,
            container,
            false
        )
        binding.viewModel = adViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        collapsingToolbar.title = getString(R.string.ad)
        mainActivity().enableNavigateButton(collapsingToolbar.toolbar)

        if (adViewModel.currentAdResource.value?.status != Status.SUCCESS ||
            mainActivity().updateStatuses.isNeedToUpdateAd) {
            extractArguments()
            adViewModel.loadAd()
            mainActivity().updateStatuses.isNeedToUpdateAd = false
        }
        setFavouriteButtonDrawable()
        toggleMenu()

        setListeners()
        setObservers()
    }

    private fun setListeners() {
        profilePanel.setOnClickListener { navigateToProfile() }

        commentBtn.setOnClickListener { navigateToComments() }

        favouriteBtn.setOnClickListener {
            adViewModel.compactAd.apply { isFavourite = !isFavourite }
            setFavouriteButtonDrawable()
            adsListViewModel.toggleFavourite(adViewModel.compactAd)
        }

        setFragmentResultListener(DELETE_AD_RESULT_REQUEST_KEY) { _, bundle ->
            if (bundle.getBoolean(RESULT_YES_NO_KEY)) {
                adViewModel.deleteAd()
            }
        }
    }

    private fun setObservers() {
        adViewModel.currentAdResource.observe(viewLifecycleOwner, {
            when(it.status) {
                Status.SUCCESS -> {
                    swipeContainer.isRefreshing = false
                    setFavouriteButtonDrawable()
                }
                Status.ERROR -> {
                    swipeContainer.isRefreshing = false
                    requireContext().shortToast(it.message).show()
                }
                Status.LOADING -> swipeContainer.isRefreshing = true
            }
        })

        adsListViewModel.favouritesResource.observe(viewLifecycleOwner, {
            when(it.status) {
                Status.SUCCESS -> {
                    if (it.data!!.isFavourite) {
                        requireView().shortSnackbar(getString(R.string.added_favourites)).show()
                    } else {
                        requireView().shortSnackbar(getString(R.string.removed_favourites)).show()
                    }
                }
                Status.ERROR -> {
                    adViewModel.compactAd.apply { isFavourite = !isFavourite }
                    setFavouriteButtonDrawable()
                    requireContext().shortToast(it.message).show()
                }
                Status.LOADING -> {}
            }
        })

        adViewModel.deleteAdResource.observe(viewLifecycleOwner, {
            when(it.status) {
                Status.SUCCESS -> {
                    swipeContainer.isRefreshing = false
                    mainActivity().updateStatuses.isNeedToUpdateAdsList = true
                    requireActivity().onBackPressed()
                }
                Status.ERROR -> {
                    swipeContainer.isRefreshing = false
                    requireContext().shortToast(it.message).show()
                }
                Status.LOADING -> swipeContainer.isRefreshing = true
            }
        })
    }

    private fun toggleMenu() {
        if (adViewModel.isOwnAd && !collapsingToolbar.toolbar.menu.hasVisibleItems()) {
            collapsingToolbar.toolbar.inflateMenu(R.menu.edit_delete)

            collapsingToolbar.toolbar.setOnMenuItemClickListener {menuItem ->
                adViewModel.currentAd.get()?.let {
                    when(menuItem.itemId) {
                        R.id.action_edit -> navigateToEdit()
                        R.id.action_delete -> showDeleteConfirmation()
                    }
                }
                false
            }
        }
    }

    private fun setFavouriteButtonDrawable() {
        if (adViewModel.compactAd.isFavourite) {
            favouriteBtn.setImageResource(R.drawable.ic_star)
        } else {
            favouriteBtn.setImageResource(R.drawable.ic_star_border)
        }
    }

    private fun extractArguments() {
        val compactAd = if (requireArguments().containsKey("compactAd")) {
            requireArguments().getSerializable("compactAd") as CompactAd
        } else {
            val ad = requireArguments().getSerializable("ad") as Ad

            val userName = ad.user?.let {
                "${it.name} ${it.surname}"
            } ?:run { null }
            adViewModel.spannedDescription.set(ad.description)

            CompactAd(
                ad.id,
                ad.name,
                ad.shortDescription,
                ad.beginTime,
                ad.endTime,
                ad.userId,
                userName,
                ad.organizationId,
                ad.organization?.name,
                ad.isFavourite
            )
        }

        adViewModel.compactAd = compactAd
        adViewModel.fillAdData()
    }

    private fun navigateToEdit() {
        adViewModel.currentAd.get()?.let {
            findNavController().navigate(
                R.id.action_adFragment_to_createEditAdFragment,
                bundleOf(MODIFY_AD_TYPE_KEY to EditAd(it))
            )
        }
    }

    private fun navigateToComments() {
        adViewModel.currentAd.get()?.let {
            findNavController().navigate(
                R.id.action_adFragment_to_commentsBottomDialog,
                bundleOf("ad" to it)
            )
        }
    }

    private fun navigateToProfile() {
        adViewModel.currentAd.get()?.organizationId?.let { // Organization`s ad
            requireView().shortSnackbar("Organizations in progress").show()
        } ?: run { // User`s ad
            adViewModel.currentAd.get()?.let {
                val bundle = bundleOf(
                    "userId" to it.userId,
                    "user" to it.user
                )
                try {
                    findNavController().navigate(R.id.action_adFragment_to_other_user, bundle)
                } catch (e: IllegalArgumentException) {
                    findNavController().navigate(R.id.otherUserFragment, bundle)
                }
            }
        }
    }

    private fun showDeleteConfirmation() {
        YesNoDialog.newInstance(
            DELETE_AD_RESULT_REQUEST_KEY,
            getString(R.string.delete_ad_confirmation)
        ).show(parentFragmentManager, null)
    }
}