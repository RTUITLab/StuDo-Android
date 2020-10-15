package ru.rtuitlab.studo.ui.general.users.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_other_user.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.rtuitlab.studo.R
import ru.rtuitlab.studo.databinding.FragmentOtherUserBinding
import ru.rtuitlab.studo.extensions.shortToast
import ru.rtuitlab.studo.server.Status
import ru.rtuitlab.studo.server.general.users.models.User
import ru.rtuitlab.studo.ui.general.ads.fragments.AdsListFragment.Companion.ADS_TYPE_KEY
import ru.rtuitlab.studo.ui.general.resumes.ResumesListFragment.Companion.RESUMES_TYPE_KEY
import ru.rtuitlab.studo.viewmodels.ads.OwnAds
import ru.rtuitlab.studo.viewmodels.ads.UserAds
import ru.rtuitlab.studo.viewmodels.resumes.OwnResumes
import ru.rtuitlab.studo.viewmodels.resumes.UserResumes
import ru.rtuitlab.studo.viewmodels.users.OtherUserViewModel

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

        toolbar.setNavigationIcon(R.drawable.ic_arrow)
        toolbar.setNavigationOnClickListener {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
            requireActivity().onBackPressed()
        }

        if (viewModel.userResource.value?.status != Status.SUCCESS) {
            extractArguments()
            viewModel.loadUser()
        }

        setListeners()
        setObservers()
    }

    private fun setListeners() {
        emailTV.setOnClickListener { sendEmail() }
        adsTV.setOnClickListener { navigateToAds() }
        resumesTV.setOnClickListener { navigateToResumes() }
    }

    private fun setObservers() {
        viewModel.userResource.observe(viewLifecycleOwner, {
            when(it.status) {
                Status.SUCCESS -> swipeContainer.isRefreshing = false
                Status.ERROR -> {
                    swipeContainer.isRefreshing = false
                    requireContext().shortToast(it.message).show()
                }
                Status.LOADING -> swipeContainer.isRefreshing = true
            }
        })
    }

    private fun navigateToAds() {
        val adsType = when {
            viewModel.isOwnProfile() -> OwnAds
            else -> UserAds(viewModel.userId)
        }
        findNavController().navigate(
            R.id.action_otherUserFragment_to_ads_nested,
            bundleOf(ADS_TYPE_KEY to adsType)
        )
    }

    private fun navigateToResumes() {
        val resumesType = when {
            viewModel.isOwnProfile() -> OwnResumes
            else -> UserResumes(viewModel.userId)
        }
        findNavController().navigate(
            R.id.action_otherUserFragment_to_resumes_nested,
            bundleOf(RESUMES_TYPE_KEY to resumesType)
        )
    }

    private fun extractArguments() {
        viewModel.userId = requireArguments().getString("userId")!!

        (requireArguments().getSerializable("user") as? User)?.let {
            viewModel.fillUserData(it)
        }
    }

    private fun sendEmail() {
        val emailUri = Uri.fromParts("mailto", viewModel.userEmail.get(), null)
        val emailIntent = Intent(Intent.ACTION_SENDTO, emailUri)
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)))
    }
}