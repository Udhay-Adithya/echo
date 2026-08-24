package com.udhay.echo.feature.tools.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhay.echo.feature.tools.domain.model.ToolDefinition
import com.udhay.echo.feature.tools.domain.usecase.DeleteToolUseCase
import com.udhay.echo.feature.tools.domain.usecase.ObserveToolsUseCase
import com.udhay.echo.feature.tools.domain.usecase.SaveToolUseCase
import com.udhay.echo.feature.tools.domain.usecase.SetToolEnabledUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ToolsViewModel(
    observeToolsUseCase: ObserveToolsUseCase,
    private val saveToolUseCase: SaveToolUseCase,
    private val setToolEnabledUseCase: SetToolEnabledUseCase,
    private val deleteToolUseCase: DeleteToolUseCase,
) : ViewModel() {

    val tools: StateFlow<List<ToolDefinition>> = observeToolsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun save(tool: ToolDefinition) {
        viewModelScope.launch { saveToolUseCase(tool) }
    }

    fun setEnabled(id: String, enabled: Boolean) {
        viewModelScope.launch { setToolEnabledUseCase(id, enabled) }
    }

    fun delete(id: String) {
        viewModelScope.launch { deleteToolUseCase(id) }
    }
}
