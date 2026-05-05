package com.udhay.kollama.feature.settings.domain.repository

import com.udhay.kollama.feature.settings.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    val settings: Flow<UserSettings>
    suspend fun getUserSettings(): UserSettings
    suspend fun saveUserSettings(settings: UserSettings)
    suspend fun clearUserSettings()
}