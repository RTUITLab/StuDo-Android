package com.rtuitlab.studo.ui.general.resumes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rtuitlab.studo.R
import com.rtuitlab.studo.databinding.FragmentCreateEditResumeBinding
import com.rtuitlab.studo.extensions.mainActivity
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.viewmodels.CreateEditResume
import com.rtuitlab.studo.viewmodels.CreateEditResumeViewModel
import com.rtuitlab.studo.viewmodels.CreateResume
import com.rtuitlab.studo.viewmodels.EditResume
import kotlinx.android.synthetic.main.fragment_create_edit_resume.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateEditResumeFragment: Fragment() {

    private val viewModel: CreateEditResumeViewModel by viewModel()

    private var createEditResume: CreateEditResume = CreateResume

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (arguments?.getSerializable(CreateEditResume::class.java.simpleName) as? CreateEditResume)?.let {
            createEditResume = it
        }
        val binding = DataBindingUtil.inflate<FragmentCreateEditResumeBinding>(
            inflater,
            R.layout.fragment_create_edit_resume,
            container,
            false
        )
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(createEditResume) {
            CreateResume -> {
                collapsingToolbar.title = getString(R.string.create_resume)
                doneBtn.setOnClickListener { viewModel.createResume() }
            }
            is EditResume -> {
                collapsingToolbar.title = getString(R.string.edit_resume)
                doneBtn.text = getString(R.string.save)
                doneBtn.setOnClickListener { viewModel.editResume() }
                viewModel.fillResumeData((createEditResume as EditResume).resume)
            }
        }
        mainActivity().enableNavigateButton(collapsingToolbar.toolbar)

        setObservers()
    }

    private fun setObservers() {
        viewModel.resumeResource.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    requireActivity().onBackPressed()

                    if (createEditResume == CreateResume) {
                        mainActivity().updateStatuses.isNeedToUpdateResumesList = true

                        val bundle = Bundle().apply {
                            putString("resumeId", it.data!!.id)
                        }
                        findNavController().navigate(R.id.action_resumesListFragment_to_resumeFragment, bundle)
                    } else if (createEditResume is EditResume) {
                        mainActivity().updateStatuses.isNeedToUpdateResume = true
                        mainActivity().updateStatuses.isNeedToUpdateResumesList = true
                    }
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {}
            }
        })
    }
}