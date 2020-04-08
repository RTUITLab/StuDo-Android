package com.rtuitlab.studo.ui.general.ads

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.rtuitlab.studo.R
import com.rtuitlab.studo.adapters.AdsRecyclerAdapter
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.ads.models.CompactAdWithBookmark
import com.rtuitlab.studo.viewmodels.AdsType
import com.rtuitlab.studo.viewmodels.AdsViewModel
import com.rtuitlab.studo.viewmodels.AllAds
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdsListFragment : Fragment(), AdsRecyclerAdapter.OnAdClickListener {

    val viewModel: AdsViewModel by viewModel()

    private var recyclerAdapter: AdsRecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recycler_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collapsingToolbar.title = getString(R.string.title_ads)
        if (viewModel.adsListResource.value == null) {
            loadAds()
        } else {
            initRecyclerView()
        }
        setListeners()
        setObservers()
    }

    private fun loadAds() {
//        ac461c9f-4440-4ddb-9484-ee16d2b5ffb7
        val adsType = arguments?.getSerializable("AdsType") as? AdsType ?: AllAds
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

        viewModel.bookmarksResource.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    if (it.data!!.isBookmarked) {
                        Snackbar.make(requireView(), getString(R.string.added_bookmarks), Snackbar.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(requireView(), getString(R.string.removed_bookmarks), Snackbar.LENGTH_SHORT).show()
                    }
                }
                Status.ERROR -> {
                    recyclerAdapter?.handleBookmarkError(it.data!!)
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

    override fun onAdClicked(compactAdWithBookmark: CompactAdWithBookmark) {
        Snackbar.make(requireView(), compactAdWithBookmark.ad.name, Snackbar.LENGTH_SHORT).show()
    }

    override fun onBookmarkToggle(compactAdWithBookmark: CompactAdWithBookmark) {
        viewModel.toggleBookmark(compactAdWithBookmark)
    }
}
