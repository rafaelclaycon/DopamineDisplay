package com.rafaelschmitt.dopaminedisplay.data.api

import com.rafaelschmitt.dopaminedisplay.data.models.ActiveUsersResponse
import com.rafaelschmitt.dopaminedisplay.data.models.SessionsResponse
import com.rafaelschmitt.dopaminedisplay.data.models.TopChartItem
import retrofit2.http.GET
import retrofit2.http.Path

interface MedoApiService {
    
    @GET("v3/active-users-count-from/{date}/{password}")
    suspend fun getActiveUsers(
        @Path("date") date: String,
        @Path("password") password: String
    ): ActiveUsersResponse
    
    @GET("v3/sessions-count-from/{date}/{password}")
    suspend fun getSessionsCount(
        @Path("date") date: String,
        @Path("password") password: String
    ): SessionsResponse
    
    @GET("v3/sound-share-count-stats-from/{date}")
    suspend fun getTopSharedSounds(
        @Path("date") date: String
    ): List<TopChartItem>
}

