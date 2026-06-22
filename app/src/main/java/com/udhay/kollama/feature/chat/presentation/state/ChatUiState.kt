package com.udhay.kollama.feature.chat.presentation.state

import org.udhay.ollama.api.ChatResponse
import org.udhay.ollama.api.Message
import org.udhay.ollama.api.MessageRole

sealed interface ChatUiState {
    data object Loading : ChatUiState
    data class Success(val chatResponses: List<ChatResponse>) : ChatUiState
    data class Error(
        val message: String,
        val chatResponses: List<ChatResponse> = emptyList()
    ) : ChatUiState
}

val ChatUiState.chatResponsesOrEmpty: List<ChatResponse>
    get() = when (this) {
        is ChatUiState.Loading -> emptyList()
        is ChatUiState.Success -> chatResponses
        is ChatUiState.Error -> chatResponses
    }

val ChatUiState.visibleMessages: List<Message>
    get() {
        val messages = chatResponsesOrEmpty
            .mapNotNull { it.message }
            .filter { it.content?.isNotBlank() == true }

        return when (this) {
            is ChatUiState.Error -> messages + Message(
                role = MessageRole.Assistant,
                content = message
            )
            else -> messages
        }
    }

val ChatUiState.isWaitingForAssistant: Boolean
    get() = when (this) {
        is ChatUiState.Loading -> true
        is ChatUiState.Success -> chatResponses.any { it.isPendingAssistantResponse }
        is ChatUiState.Error -> false
    }

val ChatUiState.shouldShowAssistantLoader: Boolean
    get() = when (this) {
        is ChatUiState.Loading -> true
        is ChatUiState.Success -> chatResponses.any {
            it.isPendingAssistantResponse && it.message?.content.isNullOrBlank()
        }
        is ChatUiState.Error -> false
    }

private val ChatResponse.isPendingAssistantResponse: Boolean
    get() = done == false && message?.role == MessageRole.Assistant

fun List<ChatResponse>.withoutBlankAssistantPlaceholder(): List<ChatResponse> {
    return dropLastWhile {
        it.isPendingAssistantResponse && it.message?.content.isNullOrBlank()
    }
}
