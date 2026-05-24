package com.udhay.kollama.feature.chat.presentation.state

import org.udhay.ollama.api.ChatResponse

sealed interface ChatUiState {
    data object Loading : ChatUiState
    data class Success(val chatResponses: List<ChatResponse>) : ChatUiState
    data class Error(val message: String) : ChatUiState
}
