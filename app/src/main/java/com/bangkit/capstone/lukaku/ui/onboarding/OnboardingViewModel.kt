package com.bangkit.capstone.lukaku.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.capstone.lukaku.data.repository.onboarding.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {
    fun saveOnboarding(isFinished: Boolean) {
        viewModelScope.launch {
            onboardingRepository.saveOnboarding(isFinished)
        }
    }

    fun getOnboarding(): Flow<Boolean?> = onboardingRepository.getOnboarding()
}