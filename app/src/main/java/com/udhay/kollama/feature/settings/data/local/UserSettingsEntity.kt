package com.udhay.kollama.feature.settings.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udhay.kollama.feature.chat.domain.model.OllamaModel

@Entity(tableName = "user_settings")
data class UserSettingsEntity(

    @PrimaryKey
    val id: Int = 1, // Singleton row — only one user, always ID = 1

    // Identity
    val username: String = "",
    val occupation: String = "",

    // Preferences (free-form text, e.g. "I prefer concise answers")
    val personalPreferences: String = "I prefer concise answers",

    // Theme
    val darkModeEnabled: Boolean = false,
    val amoledPaletteEnabled: Boolean = false,
    val fontScale: Float = 1f,

    // Model selection
    val selectedModel: OllamaModel? = null,

    // Server settings
    val serverHost: String = "http://localhost:11434",
    val serverHeaders: Map<String, String> = emptyMap(),

    // Generation preferences
    val thinkingEnabled: Boolean = false,
    val temperature: Float? = null,
    val topK: Int? = null,
    val topP: Float? = null,
    val numCtx: Int? = null,
    val keepAlive: String? = null,

    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
