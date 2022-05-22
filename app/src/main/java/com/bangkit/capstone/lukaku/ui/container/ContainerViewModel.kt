package com.bangkit.capstone.lukaku.ui.container

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.capstone.lukaku.data.repository.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ContainerViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    fun isReady(): Boolean {
        val result = viewModelScope.runCatching {
            val randomDelays = mutableListOf<Long>(500, 1000, 1500, 2000, 2500, 3000)
            val randomIndex = Random().nextInt(randomDelays.size)
            val randomTimeMillis = randomDelays[randomIndex]
            runBlocking {
                delay(randomTimeMillis)
            }
            true
        }
        return result.isSuccess
    }

    fun getThemeSetting(): Flow<Boolean?> = settingsRepository.getThemeSetting()
}