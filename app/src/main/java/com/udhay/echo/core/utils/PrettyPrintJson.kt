package com.udhay.echo.core.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

fun prettyPrintJson(json: JsonElement?): String {
    if (json == null) return "No details"

    return try {
        Json { prettyPrint = true }.encodeToString(JsonElement.serializer(), json)
    } catch (e: Exception) {
        json.toString()
    }
}