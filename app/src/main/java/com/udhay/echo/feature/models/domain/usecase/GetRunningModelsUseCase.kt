package com.udhay.echo.feature.models.domain.usecase

import com.udhay.echo.feature.models.domain.model.RunningModel
import com.udhay.echo.feature.models.domain.repository.ModelManagementRepository
import org.koin.core.annotation.Factory

@Factory
class GetRunningModelsUseCase(private val repository: ModelManagementRepository) {
    suspend operator fun invoke(): List<RunningModel> = repository.runningModels()
}
