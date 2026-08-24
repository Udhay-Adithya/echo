package com.udhay.echo.feature.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhay.echo.feature.settings.domain.model.UserSettings
import com.udhay.echo.feature.settings.domain.usecase.ClearUserSettingsUseCase
import com.udhay.echo.feature.settings.domain.usecase.GetUserSettingsUseCase
import com.udhay.echo.feature.settings.domain.usecase.SaveUserSettingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class UserSettingsViewModel(
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val saveUserSettingsUseCase: SaveUserSettingsUseCase,
    private val clearUserSettingsUseCase: ClearUserSettingsUseCase
) : ViewModel() {

    val settings: StateFlow<UserSettings> = getUserSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(500),
            initialValue = UserSettings()
        )

    fun save(settings: UserSettings) {
        viewModelScope.launch { saveUserSettingsUseCase(settings) }
    }

    fun reset() {
        viewModelScope.launch { clearUserSettingsUseCase() }
    }
}
