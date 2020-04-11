package com.rtuitlab.studo.ui.general.ads

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rtuitlab.studo.R
import com.rtuitlab.studo.adapters.AdsRecyclerAdapter
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.ads.models.AdIdWithIsFavourite
import com.rtuitlab.studo.server.general.ads.models.CompactAd
import com.rtuitlab.studo.viewmodels.*
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
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
            BookmarkedAds -> {
                collapsingToolbar.title = getString(R.string.favourites)
                createBtn.hide()
            }
            is MyAds -> {
                collapsingToolbar.title = getString(R.string.my_ads)
            }
            is UserAds -> {
                // TODO - add title in toolbar
                createBtn.hide()
            }
        }

        if (viewModel.adsListResource.value == null) {
            loadAds()
        } else {
            initRecyclerView()
        }

        setListeners()
        setObservers()
    }

    private fun loadAds() {
        viewModel.loadAdsList(adsType)
    }

    private fun setListeners() {
        swipeContainer.setOnRefreshListener {
            loadAds()
        }
    }

    private fun setObservers() {
        viewModel.adsListResource.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    swipeContainer.isRefreshing = false
                    initRecyclerView()
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
        recyclerAdapter = AdsRecyclerAdapter(viewModel.adsListResource.value!!.data!!).apply {
            setOnAdClickListener(this@AdsListFragment)
        }
        recyclerView.adapter = recyclerAdapter
    }

    override fun onAdClicked(compactAd: CompactAd) {
        val bundle = Bundle().apply {
            putString("adId", compactAd.id)
        }
        findNavController().navigate(R.id.action_adsListFragment_to_adFragment, bundle)
    }

    override fun onFavouriteToggle(compactAd: CompactAd) {
        viewModel.toggleFavourite(AdIdWithIsFavourite(
            compactAd.id, compactAd.isFavourite
        ))
    }
}
