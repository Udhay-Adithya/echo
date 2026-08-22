package com.udhay.echo.feature.chat.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatEntity::class,
            parentColumns = ["id"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("chatId")]
)
data class ChatMessageEntity(

    @PrimaryKey
    val id: String,

    val chatId: String,

    // Serialized MessageRole (system/user/assistant/tool)
    val role: String,

    val content: String,
    val thinking: String? = null,
    val images: List<String>? = null,

    // Tool calling (toolCalls serialized as JSON)
    val toolCallsJson: String? = null,
    val toolName: String? = null,

    // Flattened assistant metadata (null for user messages)
    val model: String? = null,
    val totalDuration: Long? = null,
    val loadDuration: Long? = null,
    val promptEvalCount: Int? = null,
    val promptEvalDuration: Long? = null,
    val evalCount: Int? = null,
    val evalDuration: Long? = null,

    val createdAt: Long = System.currentTimeMillis()
)
