package ru.rtuitlab.studo.ui.general.ads.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_comments.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.rtuitlab.studo.R
import ru.rtuitlab.studo.databinding.FragmentCommentsBinding
import ru.rtuitlab.studo.extensions.shortToast
import ru.rtuitlab.studo.recyclers.comments.CommentsRecyclerAdapter
import ru.rtuitlab.studo.server.Status
import ru.rtuitlab.studo.server.general.ads.models.Ad
import ru.rtuitlab.studo.server.general.ads.models.Comment
import ru.rtuitlab.studo.ui.general.YesNoDialog
import ru.rtuitlab.studo.ui.general.YesNoDialog.Companion.RESULT_YES_NO_KEY
import ru.rtuitlab.studo.viewmodels.ads.CommentsViewModel


class CommentsBottomDialog: BottomSheetDialogFragment(), CommentsRecyclerAdapter.OnCommentClickListener {

    companion object {
        private const val DELETE_COMMENT_RESULT_REQUEST_KEY = "DELETE_COMMENT_RESULT_REQUEST"
    }

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

    private fun setListeners() {
        closeBtn.setOnClickListener { dismiss() }

        setFragmentResultListener(DELETE_COMMENT_RESULT_REQUEST_KEY) { _, bundle ->
            if (bundle.getBoolean(RESULT_YES_NO_KEY)) {
                viewModel.deleteComment()
            }
        }
    }

    private fun setObservers() {
        viewModel.createCommentResource.observe(viewLifecycleOwner, {
            when(it.status) {
                Status.SUCCESS -> {
                    viewModel.isValid.set(true)
                    addComment(it.data!!)
                    viewModel.commentText.set("")
                }
                Status.ERROR -> {
                    viewModel.isValid.set(true)
                    requireContext().shortToast(it.message).show()
                }
                Status.LOADING -> viewModel.isValid.set(false)
            }
        })

        viewModel.deleteCommentResource.observe(viewLifecycleOwner, {
            when(it.status) {
                Status.SUCCESS -> deleteComment(it.data!!)
                Status.ERROR -> requireContext().shortToast(it.message).show()
                Status.LOADING -> {}
            }
        })
    }

    private fun initRecyclerView() {
        recyclerAdapter = CommentsRecyclerAdapter(commentsMutableList.toList()).apply {
            setOnCommentClickListener(this@CommentsBottomDialog)
        }
        commentRV.adapter = recyclerAdapter
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
        val bundle = bundleOf("userId" to comment.authorId)
        try {
            findNavController().navigate(R.id.action_commentsBottomDialog_to_other_user, bundle)
        } catch (e: IllegalArgumentException) {
            findNavController().navigate(R.id.otherUserFragment, bundle)
        }
    }

    override fun onDeleteComment(comment: Comment) {
        viewModel.commentIdForDeleting = comment.id
        YesNoDialog.newInstance(
            DELETE_COMMENT_RESULT_REQUEST_KEY,
            getString(R.string.delete_comment_confirmation)
        ).show(parentFragmentManager, null)
    }
}