package com.rtuitlab.studo.ui.general.ads.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rtuitlab.studo.R
import com.rtuitlab.studo.recyclers.ads.AdsRecyclerAdapter
import com.rtuitlab.studo.extensions.mainActivity
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.ads.models.CompactAd
import com.rtuitlab.studo.viewmodels.ads.*
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdsListFragment : Fragment(), AdsRecyclerAdapter.OnAdClickListener {

    private val viewModel: AdsListViewModel by viewModel()

    private var recyclerAdapter: AdsRecyclerAdapter? = null

    private var adsType: AdsType = AllAds

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (arguments?.getSerializable(AdsType::class.java.simpleName) as? AdsType)?.let {
            adsType = it
        }
        return inflater.inflate(R.layout.fragment_recycler_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(adsType) {
            AllAds -> {
                collapsingToolbar.title = getString(R.string.title_ads)
            }
            FavouritesAds -> {
                collapsingToolbar.title = getString(R.string.favourites)
                mainActivity().enableNavigateButton(collapsingToolbar.toolbar)
                createBtn.hide()
            }
            is MyAds -> {
                collapsingToolbar.title = getString(R.string.my_ads)
                mainActivity().enableNavigateButton(collapsingToolbar.toolbar)
            }
            is UserAds -> {
                collapsingToolbar.title = getString(R.string.user_ads)
                mainActivity().enableNavigateButton(collapsingToolbar.toolbar)
                createBtn.hide()
            }
        }


        initRecyclerView()

        if (viewModel.adsListResource.value?.status != Status.SUCCESS ||
            mainActivity().updateStatuses.isNeedToUpdateAdsList) {
            loadAds()
            mainActivity().updateStatuses.isNeedToUpdateAdsList = false
        } else {
            recyclerAdapter?.data = viewModel.adsListResource.value!!.data!!
        }

        setListeners()
        setObservers()
    }

    private fun loadAds() {
        viewModel.loadAdsList(adsType)
    }

    private fun setListeners() {
        swipeContainer.setOnRefreshListener { loadAds() }

        createBtn.setOnClickListener { navigateToCreateAd() }
    }

    private fun setObservers() {
        viewModel.adsListResource.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    swipeContainer.isRefreshing = false
                    recyclerAdapter?.data = it.data!!
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

        viewModel.favouritesResource.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    if (it.data!!.isFavourite) {
                        Snackbar.make(requireView(), getString(R.string.added_favourites), Snackbar.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(requireView(), getString(R.string.removed_favourites), Snackbar.LENGTH_SHORT).show()
                    }
                }
                Status.ERROR -> {
                    recyclerAdapter?.handleFavouriteError(it.data!!)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {}
            }
        })
    }

    private fun initRecyclerView() {
        if (recyclerAdapter == null) {
            recyclerAdapter = AdsRecyclerAdapter()
                .apply {
                setOnAdClickListener(this@AdsListFragment)
            }
        }
        recyclerView.adapter = recyclerAdapter
    }

    override fun onAdClicked(compactAd: CompactAd) {
        navigateToAd(compactAd)
    }

    override fun onFavouriteToggle(compactAd: CompactAd) {
        viewModel.toggleFavourite(compactAd)
    }

    private fun navigateToCreateAd() {
        findNavController().navigate(R.id.action_adsListFragment_to_createEditAdFragment)
    }

    private fun navigateToAd(compactAd: CompactAd) {
        val bundle = Bundle().apply {
            putSerializable("compactAd", compactAd)
        }
        findNavController().navigate(R.id.action_adsListFragment_to_adFragment, bundle)
    }
}
