package com.udhay.kollama.feature.tools.domain.usecase

import com.udhay.kollama.feature.tools.domain.model.ToolDefinition
import com.udhay.kollama.feature.tools.domain.repository.ToolRepository
import org.koin.core.annotation.Factory

@Factory
class GetEnabledToolsUseCase(private val repository: ToolRepository) {
    suspend operator fun invoke(): List<ToolDefinition> = repository.getEnabledTools()
}
