package com.rtuitlab.studo.ui.general.resumes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.rtuitlab.studo.R
import com.rtuitlab.studo.recyclers.resumes.ResumesRecyclerAdapter
import com.rtuitlab.studo.extensions.mainActivity
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.resumes.models.CompactResume
import com.rtuitlab.studo.viewmodels.resumes.*
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResumesListFragment : Fragment(), ResumesRecyclerAdapter.OnResumeClickListener {

    private val resumesListViewModel: ResumesListViewModel by viewModel()

    private var recyclerAdapter: ResumesRecyclerAdapter? = null

    private var resumesType: ResumesType = AllResumes

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (arguments?.getSerializable(ResumesType::class.java.simpleName) as? ResumesType)?.let {
            resumesType = it
        }
        return inflater.inflate(R.layout.fragment_recycler_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(resumesType) {
            AllResumes -> {
                collapsingToolbar.title = getString(R.string.title_resumes)
            }
            is MyResumes -> {
                collapsingToolbar.title = getString(R.string.my_resumes)
                mainActivity().enableNavigateButton(collapsingToolbar.toolbar)
            }
            is UserResumes -> {
                // TODO - add title in toolbar
                mainActivity().enableNavigateButton(collapsingToolbar.toolbar)
                createBtn.hide()
            }
        }

        initRecyclerView()

        if (resumesListViewModel.resumesListResource.value?.status != Status.SUCCESS ||
            mainActivity().updateStatuses.isNeedToUpdateResumesList) {
            loadResumes()
            mainActivity().updateStatuses.isNeedToUpdateResumesList = false
        } else {
            recyclerAdapter?.data = resumesListViewModel.resumesListResource.value!!.data!!
        }

        setListeners()
        setObservers()
    }

    private fun loadResumes() {
        resumesListViewModel.loadResumesList(resumesType)
    }

    private fun setListeners() {
        swipeContainer.setOnRefreshListener {
            loadResumes()
        }

        createBtn.setOnClickListener {
            findNavController().navigate(R.id.action_resumesListFragment_to_createEditResumeFragment)
        }
    }

    private fun setObservers() {
        resumesListViewModel.resumesListResource.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    swipeContainer.isRefreshing = false
                    recyclerAdapter?.data = it.data!!
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
        if (recyclerAdapter == null) {
            recyclerAdapter = ResumesRecyclerAdapter()
                .apply {
                setOnResumeClickListener(this@ResumesListFragment)
            }
        }
        recyclerView.adapter = recyclerAdapter
    }

    override fun onResumeClick(compactResume: CompactResume) {
        val bundle = Bundle().apply {
            putSerializable("compactResume", compactResume)
        }
        findNavController().navigate(R.id.action_resumesListFragment_to_resumeFragment, bundle)
    }
}
