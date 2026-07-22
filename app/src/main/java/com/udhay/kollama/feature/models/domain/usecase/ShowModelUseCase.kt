package com.udhay.kollama.feature.models.domain.usecase

import com.udhay.kollama.feature.models.domain.model.ModelDetail
import com.udhay.kollama.feature.models.domain.repository.ModelManagementRepository
import org.koin.core.annotation.Factory

@Factory
class ShowModelUseCase(private val repository: ModelManagementRepository) {
    suspend operator fun invoke(name: String): ModelDetail = repository.showModel(name)
}
