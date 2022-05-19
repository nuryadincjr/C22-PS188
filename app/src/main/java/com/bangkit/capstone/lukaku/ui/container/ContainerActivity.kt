package com.bangkit.capstone.lukaku.ui.container

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bangkit.capstone.lukaku.R
import com.bangkit.capstone.lukaku.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContainerActivity : AppCompatActivity() {
    private val viewModel: ContainerViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        installSplashScreenWithAnim().apply {
            setupPreDrawListener()
            checkUser()
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

    private fun checkUser() {
        val firebaseUser = auth.currentUser
        // Check if user logged in or not
        if (firebaseUser != null) {
            // User is already logged in
            startActivity(Intent(this@ContainerActivity, MainActivity::class.java))
            finish()
        }
    }
}