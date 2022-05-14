package com.bangkit.capstone.lukaku.data.repository.onboarding

import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    suspend fun saveOnboarding(isFinished: Boolean)
    fun getOnboarding(): Flow<Boolean?>
}