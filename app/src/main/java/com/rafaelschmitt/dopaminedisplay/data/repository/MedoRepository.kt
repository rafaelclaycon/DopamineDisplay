package com.rafaelschmitt.dopaminedisplay.data.repository

import com.rafaelschmitt.dopaminedisplay.BuildConfig
import com.rafaelschmitt.dopaminedisplay.data.api.RetrofitClient
import com.rafaelschmitt.dopaminedisplay.data.models.ActiveUsersResponse
import com.rafaelschmitt.dopaminedisplay.data.models.SessionsResponse
import com.rafaelschmitt.dopaminedisplay.data.models.TopChartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MedoRepository {
    
    private val apiService = RetrofitClient.apiService
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    
    private fun getTodayDateString(): String {
        return dateFormat.format(Date())
    }
    
    suspend fun getActiveUsers(): Result<ActiveUsersResponse> = withContext(Dispatchers.IO) {
        try {
            val date = getTodayDateString()
            val response = apiService.getActiveUsers(date, BuildConfig.API_PASSWORD)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getSessionsCount(): Result<SessionsResponse> = withContext(Dispatchers.IO) {
        try {
            val date = getTodayDateString()
            val response = apiService.getSessionsCount(date, BuildConfig.API_PASSWORD)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getTopSharedSounds(): Result<List<TopChartItem>> = withContext(Dispatchers.IO) {
        try {
            val date = getTodayDateString()
            val response = apiService.getTopSharedSounds(date)
            // Return top 3 only
            Result.success(response.take(3))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getDashboardData(): Result<Triple<ActiveUsersResponse, SessionsResponse, List<TopChartItem>>> {
        return try {
            val activeUsersResult = getActiveUsers()
            val sessionsResult = getSessionsCount()
            val topSoundsResult = getTopSharedSounds()
            
            if (activeUsersResult.isSuccess && sessionsResult.isSuccess && topSoundsResult.isSuccess) {
                Result.success(
                    Triple(
                        activeUsersResult.getOrThrow(),
                        sessionsResult.getOrThrow(),
                        topSoundsResult.getOrThrow()
                    )
                )
            } else {
                val exception = activeUsersResult.exceptionOrNull() 
                    ?: sessionsResult.exceptionOrNull() 
                    ?: topSoundsResult.exceptionOrNull()
                    ?: Exception("Unknown error")
                Result.failure(exception)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

