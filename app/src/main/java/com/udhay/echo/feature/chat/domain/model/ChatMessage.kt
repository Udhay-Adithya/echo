package com.udhay.echo.feature.chat.domain.model

import org.udhay.ollama.api.MessageRole

/**
 * A single message within a [Chat]. Backed by Room for persisted chats, or held
 * in memory only for incognito conversations.
 *
 * @property isStreaming `true` while the assistant reply is still being received.
 * @property metadata Timing/token stats, populated once an assistant reply completes.
 */
data class ChatMessage(
    val id: String,
    val chatId: String,
    val role: MessageRole,
    val content: String,
    val thinking: String? = null,
    val images: List<String>? = null,
    val isStreaming: Boolean = false,
    val metadata: ChatMessageMetadata? = null,
    // Tool calling
    val toolCalls: List<ToolCallInfo>? = null,
    val toolName: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
