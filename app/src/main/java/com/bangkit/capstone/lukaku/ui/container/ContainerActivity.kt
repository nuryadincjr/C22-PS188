package com.bangkit.capstone.lukaku.ui.container

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bangkit.capstone.lukaku.R

class ContainerActivity : AppCompatActivity() {
    private val viewModel: ContainerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreenWithAnim().apply {
            setupPreDrawListener()
        }
        setContentView(R.layout.activity_container)
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
            slideUp.interpolator = AnticipateInterpolator()
            slideUp.duration = 500L
            // Call SplashScreenView.remove at the end of the custom animation.
            slideUp.doOnEnd { splashScreenView.remove() }
            // Run animation.
            slideUp.start()
        }
    }
}