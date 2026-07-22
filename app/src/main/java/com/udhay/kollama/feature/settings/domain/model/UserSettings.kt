package com.udhay.kollama.feature.settings.domain.model

import com.udhay.kollama.feature.chat.domain.model.OllamaModel

data class UserSettings(
    val username: String = "User",
    val occupation: String = "Developer",
    val personalPreferences: String = "I prefer concise answers",
    val darkModeEnabled: Boolean = false,
    val amoledPaletteEnabled: Boolean = false,
    val fontScale: Float = 1f,
    val selectedModel: OllamaModel? = null,
    val serverHost: String = "http://localhost:11434",
    val serverHeaders: Map<String, String> = emptyMap(),

    // Generation preferences
    val thinkingEnabled: Boolean = false,
    val temperature: Float? = null,
    val topK: Int? = null,
    val topP: Float? = null,
    val numCtx: Int? = null,
    val keepAlive: String? = null
)
