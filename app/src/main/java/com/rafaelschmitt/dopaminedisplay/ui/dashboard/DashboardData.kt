package com.rafaelschmitt.dopaminedisplay.ui.dashboard

import com.rafaelschmitt.dopaminedisplay.data.models.TopChartItem

data class DashboardData(
    val activeUsers: Int,
    val avgSessionsPerUser: Double,
    val topSounds: List<TopChartItem>,
    val date: String,
    val lastUpdateTime: String
)

