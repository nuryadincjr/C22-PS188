package com.bangkit.capstone.lukaku.di

import com.bangkit.capstone.lukaku.data.repository.onboarding.OnboardingRepository
import com.bangkit.capstone.lukaku.data.repository.onboarding.OnboardingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindsOnboardingRepository(onboardingRepositoryImpl: OnboardingRepositoryImpl): OnboardingRepository
}