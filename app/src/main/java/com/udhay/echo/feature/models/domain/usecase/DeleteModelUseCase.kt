package com.udhay.echo.feature.models.domain.usecase

import com.udhay.echo.feature.models.domain.repository.ModelManagementRepository
import org.koin.core.annotation.Factory

@Factory
class DeleteModelUseCase(private val repository: ModelManagementRepository) {
    suspend operator fun invoke(name: String): Result<Unit> = repository.deleteModel(name)
}
