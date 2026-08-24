package com.udhay.echo.feature.tools.data.model

import com.udhay.echo.feature.tools.data.local.ToolEntity
import com.udhay.echo.feature.tools.domain.model.ToolDefinition
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import org.udhay.ollama.api.Tool
import org.udhay.ollama.api.ToolFunction

private val toolJson = Json { ignoreUnknownKeys = true }

fun ToolEntity.toDomain() = ToolDefinition(
    id = id,
    name = name,
    description = description,
    parameters = parameters,
    enabled = enabled,
    createdAt = createdAt
)

fun ToolDefinition.toEntity() = ToolEntity(
    id = id,
    name = name,
    description = description,
    parameters = parameters,
    enabled = enabled,
    createdAt = createdAt
)

/** Converts a stored tool into the wire `Tool` the Ollama client sends in a request. */
fun ToolDefinition.toApiTool() = Tool(
    type = "function",
    function = ToolFunction(
        name = name,
        description = description.ifBlank { null },
        parameters = runCatching { toolJson.parseToJsonElement(parameters) as? JsonObject }
            .getOrNull() ?: buildJsonObject { }
    )
)
