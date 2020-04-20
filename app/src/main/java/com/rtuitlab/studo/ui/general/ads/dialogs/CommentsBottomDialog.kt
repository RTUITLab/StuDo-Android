package com.rtuitlab.studo.ui.general.ads.dialogs

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rtuitlab.studo.R
import com.rtuitlab.studo.recyclers.comments.CommentsRecyclerAdapter
import com.rtuitlab.studo.databinding.FragmentCommentsBinding
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.server.general.ads.models.Ad
import com.rtuitlab.studo.server.general.ads.models.Comment
import com.rtuitlab.studo.ui.general.YesNoDialog
import com.rtuitlab.studo.viewmodels.ads.CommentsViewModel
import kotlinx.android.synthetic.main.fragment_comments.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Exception


class CommentsBottomDialog: BottomSheetDialogFragment(), CommentsRecyclerAdapter.OnCommentClickListener {

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
        registerForContextMenu(commentRV)
        recyclerAdapter = CommentsRecyclerAdapter(commentsMutableList.toList()).apply {
            setOnCommentClickListener(this@CommentsBottomDialog)
        }
        commentRV.adapter = recyclerAdapter
    }

    private fun setListeners() {
        closeBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun setObservers() {
        viewModel.createCommentResource.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    viewModel.isValid.set(true)
                    addComment(it.data!!)
                    viewModel.commentText.set("")
                }
                Status.ERROR -> {
                    viewModel.isValid.set(true)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {
                    viewModel.isValid.set(false)
                }
            }
        })

        viewModel.deleteCommentResource.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    deleteComment(it.data!!)
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {}
            }
        })
    }

    private fun deleteComment(commentId: String) {
        commentsMutableList.removeAll{ comment -> comment.id == commentId}

        recyclerAdapter?.updateData(commentsMutableList.toList())

        requireArguments().putSerializable("ad", viewModel.ad.apply {
            comments = commentsMutableList
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

    override fun onNavigateToProfile(comment: Comment) {
        val bundle = Bundle().apply {
            putString("userId", comment.authorId)
        }
        try {
            findNavController().navigate(R.id.action_commentsBottomDialog_to_other_user, bundle)
        } catch (e: Exception) {
            findNavController().navigate(R.id.otherUserFragment, bundle)
        }
    }

    override fun onDeleteComment(comment: Comment) {
        val dialog = YesNoDialog
            .getInstance(getString(R.string.delete_comment_confirmation), object : YesNoDialog.OnYesClickListener{
                override fun onYesClick() {
                    viewModel.deleteComment(comment.id)
                }
            })
        dialog.show(childFragmentManager, null)
    }
}