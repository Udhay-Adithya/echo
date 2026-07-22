package com.udhay.kollama.feature.chat.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class OllamaModel (
    val name: String? = null,
    val model: String? = null,
    val modifiedAt: String? = null,
    val size: Long? = null,
    val digest: String? = null,
    val details: JsonElement? = null,
    val capabilities: List<String> = emptyList()
)