package com.udhay.echo.feature.chat.presentation.state

import com.udhay.echo.feature.chat.domain.model.OllamaModel

sealed interface ModelsUiState {
    data object Loading : ModelsUiState
    data class Success(val models: List<OllamaModel>) : ModelsUiState
    data class Error(val message: String) : ModelsUiState
}
