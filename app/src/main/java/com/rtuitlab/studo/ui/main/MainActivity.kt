package com.rtuitlab.studo.ui.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.rtuitlab.studo.R
import com.rtuitlab.studo.currentUser
import com.rtuitlab.studo.extensions.setupWithNavController
import com.rtuitlab.studo.ui.auth.AuthActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

//	private var currentNavController: LiveData<NavController>? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		paintStatusBar()
		setContentView(R.layout.activity_main)

		currentUser?.let {
			if (savedInstanceState == null) {
				setupBottomNavigationBar()
			}
		} ?:run {
			startActivity(Intent(this, AuthActivity::class.java))
			finish()
		}
	}

	override fun onRestoreInstanceState(savedInstanceState: Bundle) {
		super.onRestoreInstanceState(savedInstanceState)
		setupBottomNavigationBar()
	}

	private fun setupBottomNavigationBar() {

		val controller = bottomNav.setupWithNavController(
			navGraphIds = listOf(R.navigation.ads, R.navigation.resumes, R.navigation.profile),
			fragmentManager = supportFragmentManager,
			containerId = R.id.navHostContainer,
			intent = intent
		)

//		controller.observe(this, Observer { navController ->
//			NavigationUI.setupActionBarWithNavController(this, navController)
//		})
//		currentNavController = controller
	}

	private fun paintStatusBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO){
				window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
			}
			window.statusBarColor = android.R.attr.windowBackground
		}
	}

//	override fun onSupportNavigateUp(): Boolean {
//		return currentNavController?.value?.navigateUp() ?: false
//	}
}
