package com.rtuitlab.studo.ui.general.ads.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rtuitlab.studo.R
import com.rtuitlab.studo.recyclers.comments.CommentsRecyclerAdapter
import com.rtuitlab.studo.databinding.FragmentCommentsBinding
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.ads.models.Ad
import com.rtuitlab.studo.server.general.ads.models.Comment
import com.rtuitlab.studo.viewmodels.ads.CommentsViewModel
import kotlinx.android.synthetic.main.fragment_comments.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class CommentsBottomDialog: BottomSheetDialogFragment() {

    private val viewModel: CommentsViewModel by viewModel()

    private var commentsMutableList: MutableList<Comment> = mutableListOf()

    private var recyclerAdapter: CommentsRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ad = (requireArguments().getSerializable("ad") as Ad)
        commentsMutableList = ad.comments.toMutableList()
        viewModel.ad = ad
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil
            .inflate<FragmentCommentsBinding>(
                inflater,
                R.layout.fragment_comments,
                container,
                false
            )
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        setListeners()
        setObservers()
    }

    private fun initRecyclerView() {
        recyclerAdapter =
            CommentsRecyclerAdapter(
                commentsMutableList.toList()
            )
        commentRV.adapter = recyclerAdapter
    }

    private fun setListeners() {
        closeBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun setObservers() {
        viewModel.commentResource.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    addComment(it.data!!)
                    viewModel.commentText.set("")
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {}
            }
        })
    }

    private fun addComment(comment: Comment) {
        commentsMutableList.add(comment)

        recyclerAdapter?.updateData(commentsMutableList.toList()) {
            commentRV.smoothScrollToPosition(commentsMutableList.size)
        }

        requireArguments().putSerializable("ad", viewModel.ad.apply {
            comments = commentsMutableList
        })
    }
}