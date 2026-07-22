package com.udhay.kollama.feature.models.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhay.kollama.feature.models.domain.model.ModelDetail
import com.udhay.kollama.feature.models.domain.model.PullProgress
import com.udhay.kollama.feature.models.domain.usecase.CopyModelUseCase
import com.udhay.kollama.feature.models.domain.usecase.CreateModelUseCase
import com.udhay.kollama.feature.models.domain.usecase.DeleteModelUseCase
import com.udhay.kollama.feature.models.domain.usecase.GetRunningModelsUseCase
import com.udhay.kollama.feature.models.domain.usecase.ListModelsUseCase
import com.udhay.kollama.feature.models.domain.usecase.PullModelUseCase
import com.udhay.kollama.feature.models.domain.usecase.PushModelUseCase
import com.udhay.kollama.feature.models.domain.usecase.ShowModelUseCase
import com.udhay.kollama.feature.models.presentation.state.ModelManagementUiState
import com.udhay.kollama.feature.models.presentation.state.TransferKind
import com.udhay.kollama.feature.models.presentation.state.TransferUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ModelManagementViewModel(
    private val listModelsUseCase: ListModelsUseCase,
    private val getRunningModelsUseCase: GetRunningModelsUseCase,
    private val showModelUseCase: ShowModelUseCase,
    private val deleteModelUseCase: DeleteModelUseCase,
    private val copyModelUseCase: CopyModelUseCase,
    private val pullModelUseCase: PullModelUseCase,
    private val pushModelUseCase: PushModelUseCase,
    private val createModelUseCase: CreateModelUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModelManagementUiState())
    val uiState: StateFlow<ModelManagementUiState> = _uiState.asStateFlow()

    private val _detail = MutableStateFlow<ModelDetail?>(null)
    val detail: StateFlow<ModelDetail?> = _detail.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val models = listModelsUseCase()
                val running = runCatching { getRunningModelsUseCase() }.getOrDefault(emptyList())
                _uiState.update {
                    it.copy(models = models, running = running, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Couldn't reach the Ollama server")
                }
            }
        }
    }

    fun showDetails(name: String) {
        viewModelScope.launch {
            runCatching { showModelUseCase(name) }
                .onSuccess { _detail.value = it }
                .onFailure { _uiState.update { s -> s.copy(error = "Couldn't load model details") } }
        }
    }

    fun dismissDetails() {
        _detail.value = null
    }

    fun delete(name: String) {
        viewModelScope.launch {
            deleteModelUseCase(name)
                .onSuccess {
                    _uiState.update { it.copy(message = "Deleted $name") }
                    refresh()
                }
                .onFailure { _uiState.update { it.copy(error = "Couldn't delete $name") } }
        }
    }

    fun copy(source: String, destination: String) {
        if (destination.isBlank()) return
        viewModelScope.launch {
            copyModelUseCase(source, destination.trim())
                .onSuccess {
                    _uiState.update { it.copy(message = "Copied to $destination") }
                    refresh()
                }
                .onFailure { _uiState.update { it.copy(error = "Couldn't copy $source") } }
        }
    }

    fun pull(name: String) {
        if (name.isBlank()) return
        runTransfer(name.trim(), TransferKind.PULL) { pullModelUseCase(it) }
    }

    fun push(name: String) {
        runTransfer(name, TransferKind.PUSH) { pushModelUseCase(it) }
    }

    fun create(name: String, fromModel: String, system: String?) {
        if (name.isBlank() || fromModel.isBlank()) return
        runTransfer(name.trim(), TransferKind.CREATE) {
            createModelUseCase(it, fromModel.trim(), system?.ifBlank { null })
        }
    }

    private fun runTransfer(
        name: String,
        kind: TransferKind,
        source: (String) -> Flow<PullProgress>,
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    transfer = TransferUiState(name, kind, PullProgress(status = "starting")),
                    error = null
                )
            }
            source(name)
                .catch { e ->
                    _uiState.update {
                        it.copy(transfer = null, error = e.message ?: "Transfer failed")
                    }
                }
                .collect { progress ->
                    if (progress.error != null) {
                        _uiState.update { it.copy(transfer = null, error = progress.error) }
                    } else {
                        _uiState.update {
                            it.copy(transfer = TransferUiState(name, kind, progress))
                        }
                    }
                }
            // Completed
            if (_uiState.value.transfer != null) {
                _uiState.update { it.copy(transfer = null, message = "$name ${kind.pastTense()}") }
                refresh()
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, message = null) }
    }

    private fun TransferKind.pastTense() = when (this) {
        TransferKind.PULL -> "downloaded"
        TransferKind.PUSH -> "pushed"
        TransferKind.CREATE -> "created"
    }
}
