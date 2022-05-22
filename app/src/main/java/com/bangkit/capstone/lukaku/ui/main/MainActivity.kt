package com.bangkit.capstone.lukaku.ui.main

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.bangkit.capstone.lukaku.R
import com.bangkit.capstone.lukaku.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreenWithAnim().apply {
            lifecycleScope.launch {
                viewModel.getThemeSetting().collect { isDarkModeActive ->
                    val theme = if (isDarkModeActive == true) {
                        MODE_NIGHT_YES
                    } else MODE_NIGHT_NO

                    setDefaultNightMode(theme)
                }
            }
            setupPreDrawListener()
        }

        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.fragmentContainerView)
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

    private fun setupPreDrawListener() {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check if the initial data is ready.
                    return if (viewModel.isReady()) {
                        // The content is ready; start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        // The content is not ready; suspend.
                        false
                    }
                }
            }
        )
    }

    private fun installSplashScreenWithAnim() {
        installSplashScreen().setOnExitAnimationListener { splashScreenView ->
            // Create custom animation.
            splashScreenView.iconView.animate().rotation(180F).duration = 500L
            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView.iconView,
                View.TRANSLATION_Y,
                0f,
                -splashScreenView.iconView.height.toFloat()
            )

            slideUp.apply {
                interpolator = AnticipateInterpolator()
                duration = 500L
                doOnEnd { splashScreenView.remove() }
                start()
            }
        }
    }
}