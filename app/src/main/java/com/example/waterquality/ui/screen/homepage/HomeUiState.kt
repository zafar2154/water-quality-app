package com.example.waterquality.ui.screen.homepage

import com.example.waterquality.data.model.SensorResponse

data class HomeUiState(
    val currentData: SensorResponse = SensorResponse(0f, 0f, 0f),
    val phHistory: List<Float> = emptyList(),
    val tdsHistory: List<Float> = emptyList(),
    val tempHistory: List<Float> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)