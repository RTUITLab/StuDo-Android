package com.rtuitlab.studo.ui.general.resumes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.rtuitlab.studo.R
import com.rtuitlab.studo.databinding.FragmentResumeBinding
import com.rtuitlab.studo.extensions.mainActivity
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.viewmodels.CreateEditResume
import com.rtuitlab.studo.viewmodels.EditResume
import com.rtuitlab.studo.viewmodels.ResumeViewModel
import kotlinx.android.synthetic.main.fragment_resume.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResumeFragment: Fragment() {

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
            viewModel.resumeId = requireArguments().getString("resumeId")!!
            loadResume()
            mainActivity().updateStatuses.isNeedToUpdateResume = false
        }

        setListeners()
        setObservers()
    }

    private fun setListeners() {
        profilePanel.setOnClickListener {

        }

        swipeContainer.setOnRefreshListener {
            loadResume()
        }

        editBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable(CreateEditResume::class.java.simpleName, EditResume(viewModel.currentResume.get()!!))
            }
            findNavController().navigate(R.id.action_resumeFragment_to_createEditResumeFragment, bundle)
        }
    }

    private fun setObservers() {
        viewModel.currentResumeResource.observe(viewLifecycleOwner, Observer {
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

    private fun loadResume() {
        viewModel.loadResume()
    }
}