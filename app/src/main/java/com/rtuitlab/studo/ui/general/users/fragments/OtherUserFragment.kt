package com.rtuitlab.studo.ui.general.users.fragments

import android.os.Bundle
import android.util.Log
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
import com.rtuitlab.studo.databinding.FragmentOtherUserBinding
import com.rtuitlab.studo.extensions.mainActivity
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.users.models.User
import com.rtuitlab.studo.viewmodels.ads.AdsType
import com.rtuitlab.studo.viewmodels.ads.UserAds
import com.rtuitlab.studo.viewmodels.resumes.ResumesType
import com.rtuitlab.studo.viewmodels.resumes.UserResumes
import com.rtuitlab.studo.viewmodels.users.OtherUserViewModel
import kotlinx.android.synthetic.main.fragment_other_user.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class OtherUserFragment: Fragment() {

    private val viewModel: OtherUserViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentOtherUserBinding>(
            inflater,
            R.layout.fragment_other_user,
            container,
            false
        )
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collapsingToolbar.title = getString(R.string.title_profile)
        mainActivity().enableNavigateButton(collapsingToolbar.toolbar)

        if (viewModel.userResource.value?.status != Status.SUCCESS) {
            extractArguments()
            viewModel.loadUser()
        }

        setListeners()
        setObservers()
    }

    private fun setListeners() {
        adsTV.setOnClickListener { navigateToAds() }
        resumesTV.setOnClickListener { navigateToResumes() }
    }

    private fun setObservers() {
        viewModel.userResource.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    swipeContainer.isRefreshing = false
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

    private fun navigateToAds() {
        val bundle = Bundle().apply {
            putSerializable(
                AdsType::class.java.simpleName,
                UserAds(viewModel.userId)
            )
        }
        if (findNavController().graph.id == R.id.ads) {
            findNavController().navigate(R.id.action_otherUserFragment_to_adsListFragment, bundle)
        } else {
            Snackbar.make(requireView(), "WORK IN PROGRESS", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun navigateToResumes() {
        val bundle = Bundle().apply {
            putSerializable(
                ResumesType::class.java.simpleName,
                UserResumes(viewModel.userId)
            )
        }
        if (findNavController().graph.id == R.id.resumes) {
            findNavController().navigate(R.id.action_otherUserFragment2_to_resumesListFragment, bundle)
        } else {
            Snackbar.make(requireView(), "WORK IN PROGRESS", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun extractArguments() {
        viewModel.userId = requireArguments().getString("userId")!!

        (requireArguments().getSerializable("user") as? User)?.let {
            viewModel.fillUserData(it)
        }
    }
}