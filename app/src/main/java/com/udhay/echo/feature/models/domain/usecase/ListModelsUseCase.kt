package com.udhay.echo.feature.models.domain.usecase

import com.udhay.echo.feature.chat.domain.model.OllamaModel
import com.udhay.echo.feature.models.domain.repository.ModelManagementRepository
import org.koin.core.annotation.Factory

@Factory
class ListModelsUseCase(private val repository: ModelManagementRepository) {
    suspend operator fun invoke(): List<OllamaModel> = repository.listModels()
}
