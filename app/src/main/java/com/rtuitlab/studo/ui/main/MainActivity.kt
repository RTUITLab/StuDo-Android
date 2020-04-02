package com.rtuitlab.studo.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rtuitlab.studo.R
import com.rtuitlab.studo.currentUserWithToken
import com.rtuitlab.studo.ui.auth.AuthActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		currentUserWithToken?.let {
			textView.text = it.toString()
		} ?:run {
			startActivity(Intent(this, AuthActivity::class.java))
			finish()
		}
	}
}
