package com.udhay.kollama.feature.models.domain.usecase

import com.udhay.kollama.feature.chat.domain.model.OllamaModel
import com.udhay.kollama.feature.models.domain.repository.ModelManagementRepository
import org.koin.core.annotation.Factory

@Factory
class ListModelsUseCase(private val repository: ModelManagementRepository) {
    suspend operator fun invoke(): List<OllamaModel> = repository.listModels()
}
