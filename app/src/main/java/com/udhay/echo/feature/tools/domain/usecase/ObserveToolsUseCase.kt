package com.udhay.echo.feature.tools.domain.usecase

import com.udhay.echo.feature.tools.domain.model.ToolDefinition
import com.udhay.echo.feature.tools.domain.repository.ToolRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class ObserveToolsUseCase(private val repository: ToolRepository) {
    operator fun invoke(): Flow<List<ToolDefinition>> = repository.observeTools()
}
