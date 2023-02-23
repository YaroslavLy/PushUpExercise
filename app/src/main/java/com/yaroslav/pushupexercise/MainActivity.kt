package com.yaroslav.pushupexercise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController

fun AppCompatActivity.navControllers(@IdRes layout: Int) = lazy {
    val navHostFragment = supportFragmentManager.findFragmentById(layout) as NavHostFragment
    navHostFragment.navController
}

class MainActivity : AppCompatActivity() {

    private val navController by navControllers(R.id.fragmentContainerView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}