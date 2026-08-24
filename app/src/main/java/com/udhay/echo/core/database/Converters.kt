package com.udhay.echo.core.database

import androidx.room.TypeConverter
import com.udhay.echo.feature.chat.domain.model.OllamaModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromOllamaModel(model: OllamaModel?): String? {
        return model?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toOllamaModel(value: String?): OllamaModel? {
        return value?.let { json.decodeFromString<OllamaModel>(it) }
    }

    @TypeConverter
    fun fromJsonElement(element: JsonElement?): String? {
        return element?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toJsonElement(value: String?): JsonElement? {
        return value?.let { json.decodeFromString<JsonElement>(it) }
    }

    @TypeConverter
    fun fromMap(map: Map<String, String>?): String? {
        return map?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toMap(value: String?): Map<String, String>? {
        return value?.let { json.decodeFromString<Map<String, String>>(it) }
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { json.decodeFromString<List<String>>(it) }
    }
}
