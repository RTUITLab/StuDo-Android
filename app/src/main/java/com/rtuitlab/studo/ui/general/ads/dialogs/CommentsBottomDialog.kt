package com.rtuitlab.studo.ui.general.ads.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rtuitlab.studo.R
import com.rtuitlab.studo.adapters.CommentsRecyclerAdapter
import com.rtuitlab.studo.server.general.ads.models.Ad
import com.rtuitlab.studo.server.general.ads.models.Comment
import kotlinx.android.synthetic.main.fragment_comments.*

class CommentsBottomDialog: BottomSheetDialogFragment() {

    private var comments: MutableList<Comment> = mutableListOf()

    private var recyclerAdapter: CommentsRecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (comments.isEmpty()) {
            comments = (requireArguments().getSerializable("ad") as Ad).comments.toMutableList()
        }
        return inflater.inflate(R.layout.fragment_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        setListeners()
    }

    private fun initRecyclerView() {
        recyclerAdapter = CommentsRecyclerAdapter(comments)
        commentRV.adapter = recyclerAdapter
    }

    private fun setListeners() {
        closeBtn.setOnClickListener {
            dismiss()
        }
    }
}