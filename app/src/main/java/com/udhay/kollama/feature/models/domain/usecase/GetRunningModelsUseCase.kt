package com.udhay.kollama.feature.models.domain.usecase

import com.udhay.kollama.feature.models.domain.model.RunningModel
import com.udhay.kollama.feature.models.domain.repository.ModelManagementRepository
import org.koin.core.annotation.Factory

@Factory
class GetRunningModelsUseCase(private val repository: ModelManagementRepository) {
    suspend operator fun invoke(): List<RunningModel> = repository.runningModels()
}
