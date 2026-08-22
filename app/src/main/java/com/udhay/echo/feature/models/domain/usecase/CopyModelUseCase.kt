package com.udhay.echo.feature.models.domain.usecase

import com.udhay.echo.feature.models.domain.repository.ModelManagementRepository
import org.koin.core.annotation.Factory

@Factory
class CopyModelUseCase(private val repository: ModelManagementRepository) {
    suspend operator fun invoke(source: String, destination: String): Result<Unit> =
        repository.copyModel(source, destination)
}
