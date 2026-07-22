package com.udhay.kollama.feature.models.domain.usecase

import com.udhay.kollama.feature.models.domain.model.PullProgress
import com.udhay.kollama.feature.models.domain.repository.ModelManagementRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class CreateModelUseCase(private val repository: ModelManagementRepository) {
    operator fun invoke(name: String, fromModel: String, system: String?): Flow<PullProgress> =
        repository.createModel(name, fromModel, system)
}
