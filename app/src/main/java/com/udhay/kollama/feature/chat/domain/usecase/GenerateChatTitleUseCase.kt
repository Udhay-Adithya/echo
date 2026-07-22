package com.udhay.kollama.feature.chat.domain.usecase

import com.udhay.kollama.feature.chat.domain.repository.ChatRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import org.koin.core.annotation.Factory
import org.udhay.ollama.api.GenerateRequest

/**
 * Produces a short chat title from the first user message using a single-turn
 * [generate] call constrained to structured JSON output (`{"title": "..."}`).
 * Returns `null` on any failure so callers can fall back to a truncated title.
 */
@Factory
class GenerateChatTitleUseCase(
    private val chatRepository: ChatRepository
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend operator fun invoke(model: String, firstMessage: String): String? {
        val schema = buildJsonObject {
            put("type", "object")
            putJsonObject("properties") {
                putJsonObject("title") { put("type", "string") }
            }
            putJsonArray("required") { add("title") }
        }

        val prompt = buildString {
            append("Create a concise 3-6 word title summarizing the topic of this message. ")
            append("Do not use quotes or trailing punctuation. Respond with JSON only.\n\n")
            append("Message: ")
            append(firstMessage.take(600))
        }

        val response = chatRepository.generate(
            GenerateRequest(
                model = model,
                prompt = prompt,
                stream = false,
                format = schema
            )
        )

        val raw = response.response?.trim().orEmpty()
        if (raw.isBlank()) return null

        return runCatching {
            json.parseToJsonElement(raw).jsonObject["title"]?.jsonPrimitive?.content
        }.getOrNull()?.trim()?.trim('"')?.takeIf { it.isNotBlank() }
    }
}
