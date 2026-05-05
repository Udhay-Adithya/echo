package com.udhay.kollama.feature.settings.data.repository

import com.udhay.kollama.feature.settings.data.local.UserSettingsDao
import com.udhay.kollama.feature.settings.data.local.UserSettingsEntity
import com.udhay.kollama.feature.settings.data.model.toDomain
import com.udhay.kollama.feature.settings.data.model.toEntity
import com.udhay.kollama.feature.settings.domain.model.UserSettings
import com.udhay.kollama.feature.settings.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single()
class UserSettingsRepositoryImpl(
    private val dao: UserSettingsDao
) : UserSettingsRepository {

    override val settings: Flow<UserSettings> = dao
        .observeUserSettings()
        .map { it?.toDomain() ?: UserSettings() }
        .distinctUntilChanged()

    override suspend fun getUserSettings(): UserSettings {
        val entity = dao.getUserSettings() ?: UserSettingsEntity().also {
            dao.upsertUserSettings(it)
        }
        return entity.toDomain()
    }

    override suspend fun saveUserSettings(settings: UserSettings) {
        dao.upsertUserSettings(settings.toEntity())
    }

    override suspend fun clearUserSettings() {
        dao.clearUserSettings()
    }
}
