package com.udhay.echo.feature.tools.domain.usecase

import com.udhay.echo.feature.tools.domain.repository.ToolRepository
import org.koin.core.annotation.Factory

@Factory
class DeleteToolUseCase(private val repository: ToolRepository) {
    suspend operator fun invoke(id: String) = repository.deleteTool(id)
}
