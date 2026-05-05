package com.udhay.kollama.feature.settings.domain.usecase

import com.udhay.kollama.feature.settings.domain.model.UserSettings
import com.udhay.kollama.feature.settings.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Single

@Factory
class GetUserSettingsUseCase(private val repository: UserSettingsRepository) {
    operator fun invoke(): Flow<UserSettings> = repository.settings
}