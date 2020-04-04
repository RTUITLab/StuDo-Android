package com.rtuitlab.studo.ui.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rtuitlab.studo.R
import com.rtuitlab.studo.adapters.ResumesRecyclerAdapter
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.main.models.CompactResume
import com.rtuitlab.studo.viewmodels.ResumesViewModel
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ResumesListFragment : Fragment(), ResumesRecyclerAdapter.OnResumeClickListener {

    private val viewModel: ResumesViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recycler_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collapsingToolbar.title = getString(R.string.title_resumes)
        if (viewModel.resumesListResource.value == null) {
            viewModel.loadAllResumes()
        } else {
            initRecyclerView()
        }
        setListeners()
        setObservers()
    }

    private fun setListeners() {
        swipeContainer.setOnRefreshListener {
            viewModel.loadAllResumes()
        }
    }

    private fun setObservers() {
        viewModel.resumesListResource.observe(viewLifecycleOwner, Observer {
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
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ResumesRecyclerAdapter(viewModel.resumesListResource.value!!.data!!).apply {
            setOnResumeClickListener(this@ResumesListFragment)
        }
    }

    override fun onResumeClick(compactResume: CompactResume) {
        Snackbar.make(requireView(), compactResume.name, Snackbar.LENGTH_SHORT).show()
    }
}
