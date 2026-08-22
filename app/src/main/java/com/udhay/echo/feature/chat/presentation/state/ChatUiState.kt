package com.udhay.echo.feature.chat.presentation.state

import com.udhay.echo.feature.chat.domain.model.ChatMessage
import org.udhay.ollama.api.MessageRole

/**
 * Single immutable snapshot of the chat screen.
 *
 * @property currentChatId Id of the persisted chat being viewed, or `null` for a
 *   brand-new / incognito conversation that has not been saved.
 * @property messages Ordered conversation, including any in-flight streaming reply.
 * @property isIncognito When `true`, nothing in this conversation is persisted.
 * @property isStreaming `true` while an assistant reply is being received.
 * @property error User-facing error for the last send, if any.
 */
data class ChatUiState(
    val currentChatId: String? = null,
    val messages: List<ChatMessage> = emptyList(),
    val isIncognito: Boolean = false,
    val isStreaming: Boolean = false,
    val error: String? = null
)

/** Messages that have something worth rendering (hides the fully-blank streaming placeholder). */
val ChatUiState.visibleMessages: List<ChatMessage>
    get() = messages.filter {
        it.content.isNotBlank() ||
                !it.images.isNullOrEmpty() ||
                !it.thinking.isNullOrBlank() ||
                !it.toolCalls.isNullOrEmpty() ||
                it.role == MessageRole.Tool
    }

/**
 * Show a standalone loader bubble only while we wait for the first signal of a reply —
 * i.e. no content, reasoning or tool call yet.
 */
val ChatUiState.shouldShowAssistantLoader: Boolean
    get() = messages.any {
        it.isStreaming && it.content.isBlank() &&
                it.thinking.isNullOrBlank() && it.toolCalls.isNullOrEmpty()
    }

/** Disable the input bar while a reply is in flight. */
val ChatUiState.isWaitingForAssistant: Boolean
    get() = isStreaming
