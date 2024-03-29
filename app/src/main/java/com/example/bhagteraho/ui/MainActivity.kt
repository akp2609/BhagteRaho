package com.example.bhagteraho.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.bhagteraho.R
import com.example.bhagteraho.databinding.ActivityMainBinding
import com.example.bhagteraho.db.RunDAO
import com.example.bhagteraho.others.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.rootView)

        navigateToTrackingIfNeeded(intent)

        setSupportActionBar(binding.toolbar)
        val navHostFragment = findViewById<View>(R.id.NavHostFrag)
        binding.bottomNavigationView.setupWithNavController(navHostFragment.findNavController())

        navHostFragment.findNavController().addOnDestinationChangedListener{_,destination,_->
            when(destination.id){
                R.id.settingsFragment,R.id.statisticsFragment,R.id.runFragment->
                    binding.bottomNavigationView.visibility = View.VISIBLE
                else -> binding.bottomNavigationView.visibility = View.GONE
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingIfNeeded(intent)
    }

    private fun navigateToTrackingIfNeeded(intent: Intent?){
        if(intent?.action == ACTION_SHOW_TRACKING_FRAGMENT){
            findViewById<View>(R.id.NavHostFrag).findNavController().navigate(
                R.id.action_global_tracking_fragment
            )
        }
    }
}