package com.udhay.echo.feature.chat.domain.usecase

import com.udhay.echo.feature.chat.domain.repository.ModelsRepository
import org.koin.core.annotation.Factory

@Factory
class GetRunningModelNamesUseCase(
    private val repository: ModelsRepository
) {
    suspend operator fun invoke(): Set<String> = repository.getRunningModelNames()
}
