package com.rtuitlab.studo.ui.general.resumes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.rtuitlab.studo.R
import com.rtuitlab.studo.extensions.mainActivity
import com.rtuitlab.studo.extensions.shortToast
import com.rtuitlab.studo.recyclers.resumes.ResumesRecyclerAdapter
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.resumes.models.CompactResume
import com.rtuitlab.studo.viewmodels.resumes.*
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResumesListFragment : Fragment(), ResumesRecyclerAdapter.OnResumeClickListener {

    companion object {
        const val RESUMES_TYPE_KEY = "RESUMES_TYPE"
    }

    private val resumesListViewModel: ResumesListViewModel by viewModel()

    private var recyclerAdapter: ResumesRecyclerAdapter? = null

    private val resumesType by lazy {
        (arguments?.getSerializable(RESUMES_TYPE_KEY) as? ResumesType) ?: AllResumes
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

        if (resumesListViewModel.resumesListResource.value?.status != Status.SUCCESS ||
            mainActivity().updateStatuses.isNeedToUpdateResumesList) {
            loadResumes()
            mainActivity().updateStatuses.isNeedToUpdateResumesList = false
        } else {
            recyclerAdapter?.data = resumesListViewModel.resumesListResource.value!!.data!!
            checkListEmpty(resumesListViewModel.resumesListResource.value!!.data!!)
        }

        setListeners()
        setObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
    }

    private fun configScreenDependsType() {
        when(resumesType) {
            AllResumes -> {
                collapsingToolbar.title = getString(R.string.title_resumes)
            }
            is OwnResumes -> {
                collapsingToolbar.title = getString(R.string.my_resumes)
                mainActivity().enableNavigateButton(collapsingToolbar.toolbar)
            }
            is UserResumes -> {
                collapsingToolbar.title = getString(R.string.user_resumes)
                mainActivity().enableNavigateButton(collapsingToolbar.toolbar)
                createBtn.hide()
            }
        }
    }

    private fun loadResumes() {
        resumesListViewModel.loadResumesList(resumesType)
    }

    private fun setListeners() {
        swipeContainer.setOnRefreshListener { loadResumes() }

        createBtn.setOnClickListener { navigateToCreateResume() }
    }

    private fun setObservers() {
        resumesListViewModel.resumesListResource.observe(viewLifecycleOwner, {
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
    }

    private fun initRecyclerView() {
        recyclerAdapter ?: run { recyclerAdapter = get() }
        recyclerAdapter?.setOnResumeClickListener(this@ResumesListFragment)
        recyclerView.adapter = recyclerAdapter
    }

    private fun checkListEmpty(list: List<Any>) {
        if (list.isEmpty()) {
            emptyNotifier.text = getString(R.string.empty_resumes)
            emptyNotifier.visibility = View.VISIBLE
        } else {
            emptyNotifier.visibility = View.GONE
        }
    }

    override fun onResumeClick(compactResume: CompactResume) {
        navigateToResume(compactResume)
    }

    private fun navigateToCreateResume() {
        val extras = FragmentNavigatorExtras(createBtn to "create_shared_container")
        findNavController().navigate(
            R.id.action_resumesListFragment_to_createEditResumeFragment,
            null,
            null,
            extras
        )
    }

    private fun navigateToResume(compactResume: CompactResume) {
        val bundle = bundleOf("compactResume" to compactResume)
        findNavController().navigate(R.id.action_resumesListFragment_to_resumeFragment, bundle)
    }
}
