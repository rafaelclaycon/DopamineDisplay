package com.rafaelschmitt.dopaminedisplay.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelschmitt.dopaminedisplay.data.repository.MedoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: MedoRepository = MedoRepository()
) : ViewModel() {
    
    private val _state = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val state: StateFlow<DashboardState> = _state.asStateFlow()
    
    init {
        loadDashboardData()
    }
    
    fun loadDashboardData() {
        _state.value = DashboardState.Loading
        
        viewModelScope.launch {
            val result = repository.getDashboardData()
            
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
                        date = activeUsers.date
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
}

