package com.udhay.kollama.feature.chat.domain.usecase

import com.udhay.kollama.feature.chat.domain.repository.ModelsRepository
import org.koin.core.annotation.Factory

@Factory
class GetStatusUseCase(
    private val repository: ModelsRepository
) {
    suspend operator fun invoke() : Boolean{
        return repository.getStatus()
    }
}