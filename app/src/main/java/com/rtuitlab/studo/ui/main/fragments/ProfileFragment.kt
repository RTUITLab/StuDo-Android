package com.rtuitlab.studo.ui.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rtuitlab.studo.R
import com.rtuitlab.studo.currentUser
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collapsingToolbar.title = getString(R.string.title_profile)

        avatarView.text = "${currentUser!!.name[0]}${currentUser!!.surname[0]}"
        fullNameTV.text = getString(R.string.fullName, currentUser!!.name, currentUser!!.surname)
        emailTV.text = currentUser!!.email
    }
}
