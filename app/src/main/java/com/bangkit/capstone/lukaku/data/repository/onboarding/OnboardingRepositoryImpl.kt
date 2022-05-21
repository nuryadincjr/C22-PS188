package com.bangkit.capstone.lukaku.data.repository.onboarding

import androidx.lifecycle.LiveData
import com.bangkit.capstone.lukaku.data.local.OnboardingDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class OnboardingRepositoryImpl @Inject constructor(
    private val onboardingDataStore: OnboardingDataStore
) : OnboardingRepository {
    override suspend fun saveOnboarding(isFinished: Boolean) =
        onboardingDataStore.saveOnboarding(isFinished)

    override fun getOnboarding(): Flow<Boolean?> = onboardingDataStore.getOnboarding()
}