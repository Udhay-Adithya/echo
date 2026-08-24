package com.udhay.echo.feature.settings.domain.usecase

import com.udhay.echo.feature.settings.domain.model.UserSettings
import com.udhay.echo.feature.settings.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetUserSettingsUseCase(private val repository: UserSettingsRepository) {
    operator fun invoke(): Flow<UserSettings> = repository.settings
}