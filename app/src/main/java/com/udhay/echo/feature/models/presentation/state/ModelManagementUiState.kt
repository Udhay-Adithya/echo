package com.udhay.echo.feature.models.presentation.state

import com.udhay.echo.feature.chat.domain.model.OllamaModel
import com.udhay.echo.feature.models.domain.model.PullProgress
import com.udhay.echo.feature.models.domain.model.RunningModel

data class ModelManagementUiState(
    val models: List<OllamaModel> = emptyList(),
    val running: List<RunningModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val transfer: TransferUiState? = null
)

/** Progress state for an in-flight pull/push/create. */
data class TransferUiState(
    val model: String,
    val kind: TransferKind,
    val progress: PullProgress
)

enum class TransferKind { PULL, PUSH, CREATE }
