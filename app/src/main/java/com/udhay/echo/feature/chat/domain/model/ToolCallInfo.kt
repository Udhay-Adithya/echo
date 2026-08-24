package com.udhay.echo.feature.chat.domain.model

import kotlinx.serialization.Serializable

/**
 * A tool call the model requested during a chat turn.
 *
 * @property arguments The function arguments as a JSON string.
 */
@Serializable
data class ToolCallInfo(
    val name: String,
    val arguments: String
)
