package com.rtuitlab.studo.ui.general.users.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rtuitlab.studo.BuildConfig
import com.rtuitlab.studo.R
import com.rtuitlab.studo.extensions.mainActivity
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*

class AboutFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_about, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collapsingToolbar.title = getString(R.string.about)
        mainActivity().enableNavigateButton(collapsingToolbar.toolbar)

        versionLabel.text = getString(R.string.version_holder, BuildConfig.VERSION_NAME)

        setListeners()
    }

    private val viewInBrowser = View.OnClickListener {
        val browserIntent = Intent(Intent.ACTION_VIEW).apply {
            data = when(it.id) {
                R.id.vkTV -> Uri.parse(getString(R.string.vk_group_url))
                R.id.sourceTV -> Uri.parse(getString(R.string.source_code_url))
                else -> Uri.parse(getString(R.string.studo_site_url))
            }
        }
        startActivity(browserIntent)
    }

    private fun setListeners() {
        siteTV.setOnClickListener(viewInBrowser)
        vkTV.setOnClickListener(viewInBrowser)
        sourceTV.setOnClickListener(viewInBrowser)
    }
}