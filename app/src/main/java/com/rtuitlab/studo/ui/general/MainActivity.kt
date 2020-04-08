package com.rtuitlab.studo.ui.general

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.rtuitlab.studo.R
import com.rtuitlab.studo.account.AccountStorage
import com.rtuitlab.studo.extensions.setupWithNavController
import com.rtuitlab.studo.server.Status
import com.rtuitlab.studo.ui.auth.AuthActivity
import com.rtuitlab.studo.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

	private val viewModel: MainViewModel by viewModel()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		paintStatusBar()
		setContentView(R.layout.activity_main)
		if (viewModel.isLogged()) {
			if (savedInstanceState == null) {
				setupBottomNavigationBar()
			}
		} else {
			startActivity(Intent(this, AuthActivity::class.java))
			finish()
		}
	}

	override fun onRestoreInstanceState(savedInstanceState: Bundle) {
		super.onRestoreInstanceState(savedInstanceState)
		setupBottomNavigationBar()
	}

	private fun setupBottomNavigationBar() {
		bottomNav.setupWithNavController(
			navGraphIds = listOf(R.navigation.ads, R.navigation.resumes, R.navigation.profile),
			fragmentManager = supportFragmentManager,
			containerId = R.id.navHostContainer,
			intent = intent
		)
	}

	private fun paintStatusBar() {
		if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO){
			window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
		}
		window.statusBarColor = android.R.attr.windowBackground
	}
}
