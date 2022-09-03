package com.scrip0.umassmaps.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.scrip0.umassmaps.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
	}

	override fun onBackPressed() {
		val fragment =
			this.supportFragmentManager.findFragmentById(R.id.navHostFragment) as? NavHostFragment
		val currentFragment =
			fragment?.childFragmentManager?.fragments?.get(0) as? IOnBackPressed
		currentFragment?.onBackPressed()?.takeIf { !it }
			?.let { super.onBackPressed() }
	}
}

interface IOnBackPressed {
	fun onBackPressed(): Boolean
}