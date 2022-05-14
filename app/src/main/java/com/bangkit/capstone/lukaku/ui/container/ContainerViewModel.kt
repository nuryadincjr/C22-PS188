package com.bangkit.capstone.lukaku.ui.container

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.*

class ContainerViewModel : ViewModel() {
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
}