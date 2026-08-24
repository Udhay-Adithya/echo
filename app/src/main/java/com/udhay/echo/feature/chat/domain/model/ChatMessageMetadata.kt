package com.udhay.echo.feature.chat.domain.model

/**
 * Timing and token statistics for an assistant message, captured from the final
 * streaming chunk of a chat response. Surfaced by the per-message Info sheet.
 */
data class ChatMessageMetadata(
    val model: String? = null,
    val totalDuration: Long? = null,
    val loadDuration: Long? = null,
    val promptEvalCount: Int? = null,
    val promptEvalDuration: Long? = null,
    val evalCount: Int? = null,
    val evalDuration: Long? = null
)
