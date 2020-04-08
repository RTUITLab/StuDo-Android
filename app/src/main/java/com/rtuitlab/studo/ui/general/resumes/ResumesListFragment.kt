package com.rtuitlab.studo.ui.general.resumes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.rtuitlab.studo.R
import com.rtuitlab.studo.adapters.ResumesRecyclerAdapter
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.resumes.models.CompactResume
import com.rtuitlab.studo.viewmodels.*
import kotlinx.android.synthetic.main.fragment_recycler_list.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResumesListFragment : Fragment(), ResumesRecyclerAdapter.OnResumeClickListener {

    private val viewModel: ResumesViewModel by viewModel()

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
            is UserResumes -> {
                if ((resumesType as UserResumes).userId == getViewModel<MainViewModel>().accStorage.user.id) {
                    collapsingToolbar.title = getString(R.string.my_resumes)
                } else {
                    // TODO - add title in toolbar
                    createBtn.hide()
                }
            }
        }

        if (viewModel.resumesListResource.value == null) {
            loadResumes()
        } else {
            initRecyclerView()
        }

        setListeners()
        setObservers()
    }

    private fun loadResumes() {
        viewModel.loadResumesList(resumesType)
    }

    private fun setListeners() {
        swipeContainer.setOnRefreshListener {
            loadResumes()
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
        recyclerView.adapter = ResumesRecyclerAdapter(viewModel.resumesListResource.value!!.data!!).apply {
            setOnResumeClickListener(this@ResumesListFragment)
        }
    }

    override fun onResumeClick(compactResume: CompactResume) {
        Snackbar.make(requireView(), compactResume.name, Snackbar.LENGTH_SHORT).show()
    }
}
