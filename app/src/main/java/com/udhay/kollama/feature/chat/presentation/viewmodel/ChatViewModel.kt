package com.udhay.kollama.feature.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhay.kollama.feature.chat.domain.usecase.ChatWithModelStreamUseCase
import com.udhay.kollama.feature.chat.presentation.state.ChatUiState
import com.udhay.kollama.feature.settings.domain.usecase.GetUserSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.udhay.ollama.api.ChatRequest
import org.udhay.ollama.api.ChatResponse
import org.udhay.ollama.api.Message
import org.udhay.ollama.api.MessageRole

@KoinViewModel
class ChatViewModel(
    private val chatWithModelStreamUseCase: ChatWithModelStreamUseCase,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Success(emptyList()))
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            val settings = getUserSettingsUseCase().first()
            val model = settings.selectedModel?.model ?: "llama3"

            val userMessage = Message(role = MessageRole.User, content = text)
            val userResponse = ChatResponse(message = userMessage, done = true)

            // Get current list of responses or start fresh
            val currentResponses = when (val state = _uiState.value) {
                is ChatUiState.Success -> state.chatResponses
                else -> emptyList()
            }

            val messagesForRequest = currentResponses.mapNotNull { it.message } + userMessage

            _uiState.value = ChatUiState.Success(currentResponses + userResponse)

            val assistantMessagePlaceholder = Message(role = MessageRole.Assistant, content = "")
            val assistantResponsePlaceholder = ChatResponse(message = assistantMessagePlaceholder, done = false)
            
            _uiState.update { state ->
                if (state is ChatUiState.Success) {
                    ChatUiState.Success(state.chatResponses + assistantResponsePlaceholder)
                } else state
            }

            chatWithModelStreamUseCase(
                ChatRequest(
                    model = model,
                    messages = messagesForRequest,
                    stream = true
                )
            ).catch { e ->
                _uiState.value = ChatUiState.Error(e.message ?: "Unknown error")
            }.collect { response ->
                _uiState.update { state ->
                    if (state is ChatUiState.Success) {
                        val updatedResponses = state.chatResponses.toMutableList()
                        val lastIndex = updatedResponses.lastIndex
                        val lastResponse = updatedResponses[lastIndex]
                        
                        if (lastResponse.message?.role == MessageRole.Assistant) {
                            val newContent = (lastResponse.message?.content ?: "") + (response.message?.content ?: "")
                            updatedResponses[lastIndex] = response.copy(
                                message = lastResponse.message?.copy(content = newContent)
                            )
                        }
                        ChatUiState.Success(updatedResponses)
                    } else state
                }
            }
        }
    }
}
