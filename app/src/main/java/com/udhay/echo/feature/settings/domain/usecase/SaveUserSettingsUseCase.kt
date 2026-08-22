package com.udhay.echo.feature.settings.domain.usecase

import com.udhay.echo.feature.settings.domain.model.UserSettings
import com.udhay.echo.feature.settings.domain.repository.UserSettingsRepository
import org.koin.core.annotation.Single

@Single
class SaveUserSettingsUseCase(
    private val repository: UserSettingsRepository
) {
    suspend operator fun invoke(settings: UserSettings) {
        repository.saveUserSettings(settings)
    }
}