package com.maranatha.foodlergic.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.maranatha.foodlergic.R
import com.maranatha.foodlergic.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the Toolbar as the ActionBar
        setSupportActionBar(binding.toolbar)

        // Set up Navigation Controller
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Setup ActionBar with Navigation
        setupActionBarWithNavController(navController)

        // Add a listener to manage the visibility of the Toolbar (ActionBar)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment,
                R.id.onBoardingFragment,
                R.id.homeFragment,
                R.id.manageAllergiesFragment,
                R.id.loginFragment,
                R.id.registerFragment,
                R.id.predictResultFragment-> supportActionBar?.hide()

                else -> supportActionBar?.show()
            }
        }

        // Override the back button behavior
    }

    override fun onBackPressed() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Default back behavior (pop the back stack or finish activity if no fragments left)
        if (!navController.popBackStack()) {
            super.onBackPressed() // No fragment to pop, finish the activity
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
