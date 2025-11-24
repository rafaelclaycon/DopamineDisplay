package com.rafaelschmitt.dopaminedisplay.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelschmitt.dopaminedisplay.data.repository.MedoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardViewModel(
    private val repository: MedoRepository = MedoRepository()
) : ViewModel() {
    
    private val _state = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val state: StateFlow<DashboardState> = _state.asStateFlow()
    
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private var autoRefreshJob: Job? = null
    
    init {
        loadDashboardData()
        startAutoRefresh()
    }
    
    private fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(5 * 60 * 1000L) // 5 minutes in milliseconds
                loadDashboardData()
            }
        }
    }
    
    fun loadDashboardData() {
        _state.value = DashboardState.Loading
        
        viewModelScope.launch {
            val result = repository.getDashboardData()
            val currentTime = timeFormat.format(Date())
            
            _state.value = if (result.isSuccess) {
                val (activeUsers, sessions, topSounds) = result.getOrThrow()
                
                // Calculate average sessions per user
                val avgSessionsPerUser = if (activeUsers.activeUsers > 0) {
                    sessions.sessionsCount.toDouble() / activeUsers.activeUsers.toDouble()
                } else {
                    0.0
                }
                
                DashboardState.Success(
                    DashboardData(
                        activeUsers = activeUsers.activeUsers,
                        avgSessionsPerUser = avgSessionsPerUser,
                        topSounds = topSounds,
                        date = activeUsers.date,
                        lastUpdateTime = currentTime
                    )
                )
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error occurred"
                DashboardState.Error(errorMessage)
            }
        }
    }
    
    fun refresh() {
        loadDashboardData()
    }
    
    override fun onCleared() {
        super.onCleared()
        autoRefreshJob?.cancel()
    }
}

