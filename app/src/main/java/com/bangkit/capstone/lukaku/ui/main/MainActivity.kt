package com.bangkit.capstone.lukaku.ui.main

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.bangkit.capstone.lukaku.R
import com.bangkit.capstone.lukaku.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        navController = findNavController(R.id.main_fragment)
        setupActionBarWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.button_nav_menu, menu)
        binding.bottomBar.setupWithNavController(menu, navController)
        return false
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return true
    }
}