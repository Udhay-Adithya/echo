package com.udhay.echo.feature.chat.data.model

import com.udhay.echo.feature.chat.data.local.ChatEntity
import com.udhay.echo.feature.chat.data.local.ChatMessageEntity
import com.udhay.echo.feature.chat.domain.model.Chat
import com.udhay.echo.feature.chat.domain.model.ChatMessage
import com.udhay.echo.feature.chat.domain.model.ChatMessageMetadata
import com.udhay.echo.feature.chat.domain.model.ToolCallInfo
import kotlinx.serialization.json.Json
import org.udhay.ollama.api.Message
import org.udhay.ollama.api.MessageRole
import org.udhay.ollama.api.ToolCall
import org.udhay.ollama.api.ToolCallFunction

private val mapperJson = Json { ignoreUnknownKeys = true }

fun ChatEntity.toDomain() = Chat(
    id = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Chat.toEntity() = ChatEntity(
    id = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun ChatMessageEntity.toDomain() = ChatMessage(
    id = id,
    chatId = chatId,
    role = runCatching { MessageRole.valueOf(role) }.getOrDefault(MessageRole.User),
    content = content,
    thinking = thinking,
    images = images,
    isStreaming = false,
    metadata = if (hasMetadata()) {
        ChatMessageMetadata(
            model = model,
            totalDuration = totalDuration,
            loadDuration = loadDuration,
            promptEvalCount = promptEvalCount,
            promptEvalDuration = promptEvalDuration,
            evalCount = evalCount,
            evalDuration = evalDuration
        )
    } else null,
    toolCalls = toolCallsJson?.let {
        runCatching { mapperJson.decodeFromString<List<ToolCallInfo>>(it) }.getOrNull()
    },
    toolName = toolName,
    createdAt = createdAt
)

fun ChatMessage.toEntity() = ChatMessageEntity(
    id = id,
    chatId = chatId,
    role = role.name,
    content = content,
    thinking = thinking,
    images = images,
    toolCallsJson = toolCalls?.let { mapperJson.encodeToString(it) },
    toolName = toolName,
    model = metadata?.model,
    totalDuration = metadata?.totalDuration,
    loadDuration = metadata?.loadDuration,
    promptEvalCount = metadata?.promptEvalCount,
    promptEvalDuration = metadata?.promptEvalDuration,
    evalCount = metadata?.evalCount,
    evalDuration = metadata?.evalDuration,
    createdAt = createdAt
)

private fun ChatMessageEntity.hasMetadata(): Boolean =
    model != null || totalDuration != null || evalCount != null || promptEvalCount != null

/** Maps a stored/in-memory message to the wire format the Ollama client expects. */
fun ChatMessage.toApiMessage() = Message(
    role = role,
    content = content,
    images = images,
    toolName = toolName,
    toolCalls = toolCalls?.map { call ->
        ToolCall(
            type = "function",
            function = ToolCallFunction(
                name = call.name,
                arguments = runCatching { mapperJson.parseToJsonElement(call.arguments) }.getOrNull()
            )
        )
    }
)
