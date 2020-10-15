package ru.rtuitlab.studo.ui.general.ads.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.rtuitlab.studo.R
import ru.rtuitlab.studo.extensions.mainActivity
import ru.rtuitlab.studo.extensions.shortSnackbar
import ru.rtuitlab.studo.extensions.shortToast
import ru.rtuitlab.studo.recyclers.ads.AdsRecyclerAdapter
import ru.rtuitlab.studo.server.Status
import ru.rtuitlab.studo.server.general.ads.models.CompactAd
import ru.rtuitlab.studo.viewmodels.ads.*

class AdsListFragment : Fragment(), AdsRecyclerAdapter.OnAdClickListener {

    companion object {
        const val ADS_TYPE_KEY = "ADS_TYPE"
    }

    private val viewModel: AdsListViewModel by viewModel()

    private var recyclerAdapter: AdsRecyclerAdapter? = null

    private val adsType by lazy {
        (arguments?.getSerializable(ADS_TYPE_KEY) as? AdsType) ?: AllAds
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_recycler_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configScreenDependsType()
        initRecyclerView()

        if (viewModel.adsListResource.value?.status != Status.SUCCESS ||
            mainActivity().updateStatuses.isNeedToUpdateAdsList) {
            loadAds()
            mainActivity().updateStatuses.isNeedToUpdateAdsList = false
        } else {
            recyclerAdapter?.data = viewModel.adsListResource.value!!.data!!
            checkListEmpty(viewModel.adsListResource.value!!.data!!)
        }

        setListeners()
        setObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
    }

    private fun configScreenDependsType() {
        when(adsType) {
            AllAds -> {
                collapsingToolbar.title = getString(R.string.title_ads)
            }
            FavouritesAds -> {
                collapsingToolbar.title = getString(R.string.favourites)
                mainActivity().enableNavigateButton(collapsingToolbar.toolbar)
                createBtn.hide()
            }
            is OwnAds -> {
                collapsingToolbar.title = getString(R.string.my_ads)
                mainActivity().enableNavigateButton(collapsingToolbar.toolbar)
            }
            is UserAds -> {
                collapsingToolbar.title = getString(R.string.user_ads)
                mainActivity().enableNavigateButton(collapsingToolbar.toolbar)
                createBtn.hide()
            }
        }
    }

    private fun loadAds() {
        viewModel.loadAdsList(adsType)
    }

    private fun setListeners() {
        swipeContainer.setOnRefreshListener { loadAds() }

        createBtn.setOnClickListener { navigateToCreateAd() }
    }

    private fun setObservers() {
        viewModel.adsListResource.observe(viewLifecycleOwner, {
            when(it.status) {
                Status.SUCCESS -> {
                    swipeContainer.isRefreshing = false
                    checkListEmpty(it.data!!)
                    recyclerAdapter?.data = it.data
                }
                Status.ERROR -> {
                    swipeContainer.isRefreshing = false
                    requireContext().shortToast(it.message).show()
                }
                Status.LOADING -> swipeContainer.isRefreshing = true
            }
        })

        viewModel.favouritesResource.observe(viewLifecycleOwner, {
            when(it.status) {
                Status.SUCCESS -> {
                    if (it.data!!.isFavourite) {
                        requireView().shortSnackbar(getString(R.string.added_favourites))
                            .setAnchorView(createBtn)
                            .show()
                    } else {
                        requireView().shortSnackbar(getString(R.string.removed_favourites))
                            .setAnchorView(createBtn)
                            .show()
                    }
                }
                Status.ERROR -> {
                    recyclerAdapter?.handleFavouriteError(it.data!!)
                    requireContext().shortToast(it.message).show()
                }
                Status.LOADING -> {}
            }
        })
    }

    private fun initRecyclerView() {
        recyclerAdapter ?: run { recyclerAdapter = get() }
        recyclerAdapter?.setOnAdClickListener(this@AdsListFragment)
        recyclerView.adapter = recyclerAdapter
    }

    private fun checkListEmpty(list: List<Any>) {
        if (list.isEmpty()) {
            if (adsType == FavouritesAds) {
                emptyNotifier.text = getString(R.string.empty_favourites)
            } else {
                emptyNotifier.text = getString(R.string.empty_ads)
            }
            emptyNotifier.visibility = View.VISIBLE
        } else {
            emptyNotifier.visibility = View.GONE
        }
    }

    override fun onAdClicked(compactAd: CompactAd) {
        navigateToAd(compactAd)
    }

    override fun onFavouriteToggle(compactAd: CompactAd) {
        viewModel.toggleFavourite(compactAd)
    }

    private fun navigateToCreateAd() = findNavController().navigate(
        R.id.action_adsListFragment_to_createEditAdFragment,
        null,
        null,
        FragmentNavigatorExtras(createBtn to "create_shared_container")
    )

    private fun navigateToAd(compactAd: CompactAd) = findNavController().navigate(
        R.id.action_adsListFragment_to_adFragment,
        bundleOf("compactAd" to compactAd)
    )
}
