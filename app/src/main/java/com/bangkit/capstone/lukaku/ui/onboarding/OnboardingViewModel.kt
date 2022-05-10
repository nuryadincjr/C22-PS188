package com.bangkit.capstone.lukaku.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class OnboardingViewModel : ViewModel() {
    fun isReady(): Boolean {
        val result = viewModelScope.runCatching {
            runBlocking {
                delay(3000)
            }
            true
        }
        return result.isSuccess
    }
}