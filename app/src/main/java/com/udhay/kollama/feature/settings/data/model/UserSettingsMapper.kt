package com.udhay.kollama.feature.settings.data.model

import com.udhay.kollama.feature.settings.data.local.UserSettingsEntity
import com.udhay.kollama.feature.settings.domain.model.UserSettings

fun UserSettingsEntity.toDomain() = UserSettings(
    username = username,
    occupation = occupation,
    personalPreferences = personalPreferences,
    darkModeEnabled = darkModeEnabled,
    amoledPaletteEnabled = amoledPaletteEnabled,
    fontScale = fontScale,
    selectedModel = selectedModel,
    serverHost = serverHost,
    serverHeaders = serverHeaders,
    thinkingEnabled = thinkingEnabled,
    temperature = temperature,
    topK = topK,
    topP = topP,
    numCtx = numCtx,
    keepAlive = keepAlive
)

fun UserSettings.toEntity() = UserSettingsEntity(
    id = 1,
    username = username,
    occupation = occupation,
    personalPreferences = personalPreferences,
    darkModeEnabled = darkModeEnabled,
    amoledPaletteEnabled = amoledPaletteEnabled,
    fontScale = fontScale,
    selectedModel = selectedModel,
    serverHost = serverHost,
    serverHeaders = serverHeaders,
    thinkingEnabled = thinkingEnabled,
    temperature = temperature,
    topK = topK,
    topP = topP,
    numCtx = numCtx,
    keepAlive = keepAlive,
    updatedAt = System.currentTimeMillis()
)