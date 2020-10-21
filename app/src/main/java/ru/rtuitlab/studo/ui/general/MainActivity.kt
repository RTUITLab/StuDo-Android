package ru.rtuitlab.studo.ui.general

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.rtuitlab.studo.R
import ru.rtuitlab.studo.extensions.setupWithNavController
import ru.rtuitlab.studo.ui.BaseActivity
import ru.rtuitlab.studo.ui.auth.AuthActivity
import ru.rtuitlab.studo.utils.UpdateStatuses
import ru.rtuitlab.studo.viewmodels.MainViewModel

class MainActivity : BaseActivity() {

	private val viewModel: MainViewModel by viewModel()

	var updateStatuses = UpdateStatuses()

	override fun onCreate(savedInstanceState: Bundle?) {
		setTheme(R.style.AppTheme)
		viewModel.checkTheme()
		super.onCreate(savedInstanceState)
		paintStatusBar()
		setContentView(R.layout.activity_main)
		if (viewModel.isLogged()) {
			savedInstanceState ?: setupBottomNavigationBar()
		} else {
			startActivity(Intent(this, AuthActivity::class.java))
			finish()
		}
	}

	override fun onRestoreInstanceState(savedInstanceState: Bundle) {
		super.onRestoreInstanceState(savedInstanceState)
		updateStatuses = savedInstanceState.getSerializable(UpdateStatuses::class.java.simpleName) as UpdateStatuses
		setupBottomNavigationBar()
	}

	override fun onSaveInstanceState(outState: Bundle) {
		outState.putSerializable(UpdateStatuses::class.java.simpleName, updateStatuses)
		super.onSaveInstanceState(outState)
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
		toolbar.setNavigationOnClickListener {
			onBackPressed()
			(getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
				?.hideSoftInputFromWindow(it.windowToken, 0)
		}
	}
}
