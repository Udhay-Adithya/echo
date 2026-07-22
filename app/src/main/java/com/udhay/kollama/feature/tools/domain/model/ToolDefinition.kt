package com.udhay.kollama.feature.tools.domain.model

/**
 * A user-defined function the model may call. Mirrors an Ollama tool:
 * `{ type: "function", function: { name, description, parameters } }`.
 *
 * @property parameters JSON Schema (as text) describing the function's arguments.
 */
data class ToolDefinition(
    val id: String,
    val name: String,
    val description: String,
    val parameters: String,
    val enabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
