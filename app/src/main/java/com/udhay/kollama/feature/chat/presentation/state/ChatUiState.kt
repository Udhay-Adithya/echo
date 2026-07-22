package com.udhay.kollama.feature.chat.presentation.state

import com.udhay.kollama.feature.chat.domain.model.ChatMessage

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

/** Messages that have content worth rendering (hides the blank streaming placeholder). */
val ChatUiState.visibleMessages: List<ChatMessage>
    get() = messages.filter { it.content.isNotBlank() || !it.images.isNullOrEmpty() }

/** Show a standalone loader bubble while we wait for the first token of a reply. */
val ChatUiState.shouldShowAssistantLoader: Boolean
    get() = messages.any { it.isStreaming && it.content.isBlank() }

/** Disable the input bar while a reply is in flight. */
val ChatUiState.isWaitingForAssistant: Boolean
    get() = isStreaming
