package com.rtuitlab.studo.ui.main.fragments

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
import com.rtuitlab.studo.server.general.ads.models.CompactAd
import com.rtuitlab.studo.viewmodels.AdsViewModel
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AdsListFragment : Fragment(), AdsRecyclerAdapter.OnAdClickListener {

    val viewModel: AdsViewModel by sharedViewModel()

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
            viewModel.loadAdsList()
        } else {
            initRecyclerView()
        }
        setListeners()
        setObservers()
    }

    private fun setListeners() {
        swipeContainer.setOnRefreshListener {
            viewModel.loadAdsList()
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
    }

    private fun initRecyclerView() {
        recyclerView.adapter = AdsRecyclerAdapter(viewModel.adsListResource.value!!.data!!).apply {
            setOnAdClickListener(this@AdsListFragment)
        }
    }

    override fun onAdClicked(compactAd: CompactAd) {
        Snackbar.make(requireView(), compactAd.name, Snackbar.LENGTH_SHORT).show()
    }
}
