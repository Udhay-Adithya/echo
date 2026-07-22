package com.udhay.kollama.feature.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhay.kollama.feature.chat.domain.model.OllamaModel
import com.udhay.kollama.feature.chat.domain.usecase.GetModelCapabilitiesUseCase
import com.udhay.kollama.feature.chat.domain.usecase.GetModelsUseCase
import com.udhay.kollama.feature.chat.domain.usecase.GetRunningModelNamesUseCase
import com.udhay.kollama.feature.chat.domain.usecase.GetStatusUseCase
import com.udhay.kollama.feature.chat.presentation.state.ModelsUiState
import com.udhay.kollama.feature.settings.domain.usecase.GetUserSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ModelsViewModel(
    private val getModelsUseCase: GetModelsUseCase,
    private val getStatusUseCase: GetStatusUseCase,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val getModelCapabilitiesUseCase: GetModelCapabilitiesUseCase,
    private val getRunningModelNamesUseCase: GetRunningModelNamesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ModelsUiState>(ModelsUiState.Loading)
    val uiState: StateFlow<ModelsUiState> = _uiState

    private val _isServerOnline = MutableStateFlow<Boolean?>(null)
    val isServerOnline: StateFlow<Boolean?> = _isServerOnline

    private val _runningModels = MutableStateFlow<Set<String>>(emptySet())
    val runningModels: StateFlow<Set<String>> = _runningModels

    init {
        observeSettingsChanges()
    }

    private fun observeSettingsChanges() {
        viewModelScope.launch {
            getUserSettingsUseCase()
                .distinctUntilChanged { old, new ->
                    old.serverHost == new.serverHost && old.serverHeaders == new.serverHeaders
                }
                .collect {
                    getModels()
                }
        }
    }

    fun getModels() {
        viewModelScope.launch {
            _uiState.value = ModelsUiState.Loading
            _isServerOnline.value = null

            try {
                val result = getModelsUseCase()

                if (result.isEmpty()) {
                    _uiState.value = ModelsUiState.Error("No models found or connection issue")
                    // Check status specifically if models are empty to see if server is even up
                    getStatus()
                } else {
                    _uiState.value = ModelsUiState.Success(result)
                    _isServerOnline.value = true
                    _runningModels.value = getRunningModelNamesUseCase()
                }
            } catch (e: Exception) {
                _uiState.value = ModelsUiState.Error("Failed to connect to Ollama server")
                _isServerOnline.value = false
            }
        }
    }

    /**
     * Resolves the chosen model's capabilities (via /api/show) before persisting it,
     * so the chat layer knows whether the model supports thinking, tools, vision, etc.
     */
    fun onModelChosen(model: OllamaModel, save: (OllamaModel) -> Unit) {
        viewModelScope.launch {
            val capabilities = getModelCapabilitiesUseCase(model.model ?: model.name.orEmpty())
            save(model.copy(capabilities = capabilities))
        }
    }

    fun getStatus() {
        viewModelScope.launch {
            try {
                _isServerOnline.value = getStatusUseCase()
            } catch (e: Exception) {
                _isServerOnline.value = false
            }
        }
    }
}
