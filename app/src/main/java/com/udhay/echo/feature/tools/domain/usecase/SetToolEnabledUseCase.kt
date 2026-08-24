package com.udhay.echo.feature.tools.domain.usecase

import com.udhay.echo.feature.tools.domain.repository.ToolRepository
import org.koin.core.annotation.Factory

@Factory
class SetToolEnabledUseCase(private val repository: ToolRepository) {
    suspend operator fun invoke(id: String, enabled: Boolean) = repository.setEnabled(id, enabled)
}
