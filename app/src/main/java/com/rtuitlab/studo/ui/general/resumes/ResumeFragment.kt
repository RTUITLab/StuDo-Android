package com.rtuitlab.studo.ui.general.resumes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.rtuitlab.studo.R
import com.rtuitlab.studo.databinding.FragmentResumeBinding
import com.rtuitlab.studo.extensions.mainActivity
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.resumes.models.CompactResume
import com.rtuitlab.studo.server.general.resumes.models.Resume
import com.rtuitlab.studo.ui.general.YesNoDialog
import com.rtuitlab.studo.ui.general.YesNoDialog.Companion.RESULT_YES_NO_KEY
import com.rtuitlab.studo.ui.general.resumes.CreateEditResumeFragment.Companion.MODIFY_RESUME_TYPE_KEY
import com.rtuitlab.studo.viewmodels.resumes.EditResume
import com.rtuitlab.studo.viewmodels.resumes.ResumeViewModel
import kotlinx.android.synthetic.main.fragment_resume.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResumeFragment: Fragment() {

    companion object {
        private const val DELETE_RESUME_RESULT_REQUEST_KEY = "DELETE_RESUME_RESULT_REQUEST"
    }

    private val viewModel: ResumeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentResumeBinding>(
            inflater,
            R.layout.fragment_resume,
            container,
            false
        )
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        collapsingToolbar.title = getString(R.string.resume)
        mainActivity().enableNavigateButton(collapsingToolbar.toolbar)

        if (viewModel.currentResumeResource.value?.status != Status.SUCCESS ||
            mainActivity().updateStatuses.isNeedToUpdateResume) {
            extractArguments()
            viewModel.loadResume()
            mainActivity().updateStatuses.isNeedToUpdateResume = false
        }
        toggleMenu()

        setListeners()
        setObservers()
    }

    private fun setListeners() {
        profilePanel.setOnClickListener { navigateToProfile() }

        childFragmentManager.setFragmentResultListener(
            DELETE_RESUME_RESULT_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            if (bundle.getBoolean(RESULT_YES_NO_KEY)) {
                viewModel.deleteResume()
            }
        }
    }

    private fun setObservers() {
        viewModel.currentResumeResource.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    swipeContainer.isRefreshing = false
                    toggleMenu()
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

        viewModel.deleteResumeResource.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    swipeContainer.isRefreshing = false
                    mainActivity().updateStatuses.isNeedToUpdateResumesList = true
                    requireActivity().onBackPressed()
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

    private fun toggleMenu() {
        if (viewModel.isOwnResume && !collapsingToolbar.toolbar.menu.hasVisibleItems()) {
            collapsingToolbar.toolbar.inflateMenu(R.menu.edit_delete)

            collapsingToolbar.toolbar.setOnMenuItemClickListener {menuItem ->
                viewModel.currentResume.get()?.let {
                    when(menuItem.itemId) {
                        R.id.action_edit -> navigateToEdit()
                        R.id.action_delete -> showDeleteConfirmation()
                    }
                }
                false
            }
        }
    }

    private fun extractArguments() {
        val compactResume = if (requireArguments().containsKey("compactResume")) {
            requireArguments().getSerializable("compactResume") as CompactResume
        } else {
            val resume = requireArguments().getSerializable("resume") as Resume

            CompactResume(
                resume.id,
                resume.name,
                resume.description,
                "${resume.user.name} ${resume.user.surname}"
            )
        }

        viewModel.compactResume = compactResume
        viewModel.fillResumeData()
    }

    private fun navigateToEdit() {
        viewModel.currentResume.get()?.let {
            val bundle = bundleOf(MODIFY_RESUME_TYPE_KEY to EditResume(it))
            findNavController().navigate(R.id.action_resumeFragment_to_createEditResumeFragment, bundle)
        }
    }

    private fun navigateToProfile() {
        viewModel.currentResume.get()?.let {
            val bundle = bundleOf(
                "userId" to it.userId,
                "user" to it.user
            )
            try {
                findNavController().navigate(R.id.action_resumeFragment_to_other_user, bundle)
            } catch (e: IllegalArgumentException) {
                findNavController().navigate(R.id.otherUserFragment, bundle)
            }
        }
    }

    private fun showDeleteConfirmation() {
        YesNoDialog.newInstance(
            DELETE_RESUME_RESULT_REQUEST_KEY,
            getString(R.string.delete_resume_confirmation)
        ).show(childFragmentManager, null)
    }
}