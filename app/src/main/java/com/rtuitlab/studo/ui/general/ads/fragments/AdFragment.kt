package com.rtuitlab.studo.ui.general.ads.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rtuitlab.studo.R
import com.rtuitlab.studo.databinding.FragmentAdBinding
import com.rtuitlab.studo.extensions.mainActivity
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.ads.models.AdIdWithIsFavourite
import com.rtuitlab.studo.viewmodels.AdViewModel
import com.rtuitlab.studo.viewmodels.AdsListViewModel
import com.rtuitlab.studo.viewmodels.CreateEditAd
import com.rtuitlab.studo.viewmodels.EditAd
import kotlinx.android.synthetic.main.fragment_ad.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdFragment: Fragment() {

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
            adViewModel.adId = requireArguments().getString("adId")!!
            loadAd()
            mainActivity().updateStatuses.isNeedToUpdateAd = false
        } else {
            setFavouriteButtonDrawable(adViewModel.currentAd.get()!!.isFavourite)
        }

        setListeners()
        setObservers()
    }

    private fun setListeners() {
        profilePanel.setOnClickListener {
            if (adViewModel.currentAd.get()?.organizationId == null) { // User`s ad

            } else { // Organization`s ad
                Snackbar.make(requireView(), "Organizations in progress", Snackbar.LENGTH_SHORT).show()
            }
        }

        commentBtn.setOnClickListener {
            adViewModel.currentAd.get()?.let {
                val bundle = Bundle().apply {
                    putSerializable("ad", it)
                }
                findNavController().navigate(R.id.action_adFragment_to_commentsBottomDialog, bundle)
            }
        }

        swipeContainer.setOnRefreshListener {
            loadAd()
        }

        editBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable(CreateEditAd::class.java.simpleName, EditAd(adViewModel.currentAd.get()!!))
            }
            findNavController().navigate(R.id.action_adFragment_to_createEditAdFragment, bundle)
        }

        favouriteBtn.setOnClickListener {
            adViewModel.currentAd.get()!!.apply { isFavourite = !isFavourite }

            setFavouriteButtonDrawable(adViewModel.currentAd.get()!!.isFavourite)

            adsListViewModel.toggleFavourite(AdIdWithIsFavourite(
                adViewModel.currentAd.get()!!.id,
                adViewModel.currentAd.get()!!.isFavourite
            ))
        }
    }

    private fun setObservers() {
        adViewModel.currentAdResource.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    swipeContainer.isRefreshing = false
                    setFavouriteButtonDrawable(it.data!!.isFavourite)
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

        adsListViewModel.favouritesResource.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    if (it.data!!.isFavourite) {
                        Snackbar.make(requireView(), getString(R.string.added_favourites), Snackbar.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(requireView(), getString(R.string.removed_favourites), Snackbar.LENGTH_SHORT).show()
                    }
                }
                Status.ERROR -> {
                    adViewModel.currentAd.get()!!.apply { isFavourite = !isFavourite }
                    setFavouriteButtonDrawable(it.data!!.isFavourite)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {}
            }
        })
    }

    private fun loadAd() {
        adViewModel.loadAd()
    }

    private fun setFavouriteButtonDrawable(isFavourite: Boolean) {
        if (isFavourite) {
            favouriteBtn.setImageResource(R.drawable.ic_star)
        } else {
            favouriteBtn.setImageResource(R.drawable.ic_star_border)
        }
    }
}