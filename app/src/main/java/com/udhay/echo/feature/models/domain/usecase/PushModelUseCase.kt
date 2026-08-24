package com.udhay.echo.feature.models.domain.usecase

import com.udhay.echo.feature.models.domain.model.PullProgress
import com.udhay.echo.feature.models.domain.repository.ModelManagementRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PushModelUseCase(private val repository: ModelManagementRepository) {
    operator fun invoke(name: String): Flow<PullProgress> = repository.pushModel(name)
}
