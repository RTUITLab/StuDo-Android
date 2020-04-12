package com.rtuitlab.studo.ui.general

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.rtuitlab.studo.R
import com.rtuitlab.studo.extensions.setupWithNavController
import com.rtuitlab.studo.persistence.SettingsPreferences
import com.rtuitlab.studo.ui.auth.AuthActivity
import com.rtuitlab.studo.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class MainActivity : AppCompatActivity() {

	private val viewModel: MainViewModel by viewModel()
	private val settingsPref: SettingsPreferences by inject()

	override fun onCreate(savedInstanceState: Bundle?) {
		viewModel.checkTheme()
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

	fun enableNavigateButton(toolbar: Toolbar) {
		toolbar.setNavigationIcon(R.drawable.ic_arrow)
		toolbar.setNavigationOnClickListener { onBackPressed() }
	}



	override fun attachBaseContext(base: Context) {
		super.attachBaseContext(updateBaseContextLocale(base))
	}

	private fun updateBaseContextLocale(context: Context): Context? {
		val language= settingsPref.getSelectedLanguage()
		val locale = Locale(language)
		Locale.setDefault(locale)
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			updateResourcesLocale(context, locale)
		} else updateResourcesLocaleLegacy(context, locale)
	}

	@TargetApi(Build.VERSION_CODES.N)
	private fun updateResourcesLocale(context: Context, locale: Locale): Context? {
		val configuration: Configuration =
			context.resources.configuration
		configuration.setLocale(locale)
		return context.createConfigurationContext(configuration)
	}

	@Suppress("DEPRECATION")
	private fun updateResourcesLocaleLegacy(context: Context, locale: Locale): Context? {
		val resources: Resources = context.resources
		val configuration: Configuration = resources.configuration
		configuration.setLocale(locale)
		resources.updateConfiguration(configuration, resources.displayMetrics)
		return context
	}
}
