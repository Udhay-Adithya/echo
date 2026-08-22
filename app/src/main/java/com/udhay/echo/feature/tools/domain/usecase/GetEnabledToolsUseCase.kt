package com.udhay.echo.feature.tools.domain.usecase

import com.udhay.echo.feature.tools.domain.model.ToolDefinition
import com.udhay.echo.feature.tools.domain.repository.ToolRepository
import org.koin.core.annotation.Factory

@Factory
class GetEnabledToolsUseCase(private val repository: ToolRepository) {
    suspend operator fun invoke(): List<ToolDefinition> = repository.getEnabledTools()
}
