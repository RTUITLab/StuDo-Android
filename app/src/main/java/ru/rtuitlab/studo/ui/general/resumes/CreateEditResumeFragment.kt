package ru.rtuitlab.studo.ui.general.resumes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.fragment_create_edit_resume.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.rtuitlab.studo.R
import ru.rtuitlab.studo.databinding.FragmentCreateEditResumeBinding
import ru.rtuitlab.studo.extensions.hideProgress
import ru.rtuitlab.studo.extensions.mainActivity
import ru.rtuitlab.studo.extensions.shortToast
import ru.rtuitlab.studo.extensions.showProgress
import ru.rtuitlab.studo.server.Status
import ru.rtuitlab.studo.server.general.resumes.models.Resume
import ru.rtuitlab.studo.viewmodels.resumes.CreateEditResumeViewModel
import ru.rtuitlab.studo.viewmodels.resumes.CreateResume
import ru.rtuitlab.studo.viewmodels.resumes.EditResume
import ru.rtuitlab.studo.viewmodels.resumes.ModifyResumeType

class CreateEditResumeFragment: Fragment() {

    companion object {
        const val MODIFY_RESUME_TYPE_KEY = "MODIFY_RESUME_TYPE"
    }

    private val viewModel: CreateEditResumeViewModel by viewModel()

    private val modifyResumeType by lazy {
        (arguments?.getSerializable(MODIFY_RESUME_TYPE_KEY) as? ModifyResumeType) ?: CreateResume
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        configScreenDependsType()
        mainActivity().enableNavigateButton(collapsingToolbar.toolbar)
        setObservers()
    }

    private fun configScreenDependsType() {
        when(modifyResumeType) {
            CreateResume -> {
                collapsingToolbar.title = getString(R.string.create_resume)
                doneBtn.setOnClickListener { viewModel.createResume() }
            }
            is EditResume -> {
                collapsingToolbar.title = getString(R.string.edit_resume)
                doneBtn.text = getString(R.string.save)
                doneBtn.setOnClickListener { viewModel.editResume() }
                viewModel.fillResumeData((modifyResumeType as EditResume).resume)
            }
        }
    }

    private fun setObservers() {
        viewModel.resumeResource.observe(viewLifecycleOwner, {
            when(it.status) {
                Status.SUCCESS -> {
                    requireActivity().onBackPressed()
                    when(modifyResumeType) {
                        CreateResume -> {
                            mainActivity().updateStatuses.isNeedToUpdateResumesList = true
                            navigateToResume(it.data!!)
                        }
                        is EditResume -> {
                            mainActivity().updateStatuses.isNeedToUpdateResume = true
                            mainActivity().updateStatuses.isNeedToUpdateResumesList = true
                        }
                    }
                }
                Status.ERROR -> {
                    when(modifyResumeType) {
                        CreateResume -> doneBtn.hideProgress(R.string.create)
                        is EditResume -> doneBtn.hideProgress(R.string.save)
                    }
                    requireContext().shortToast(it.message).show()
                }
                Status.LOADING -> doneBtn.showProgress()
            }
        })
    }

    private fun navigateToResume(resume: Resume) {
        val bundle = bundleOf("resume" to resume)
        findNavController().navigate(R.id.action_resumesListFragment_to_resumeFragment, bundle)
    }
}