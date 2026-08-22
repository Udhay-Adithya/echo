package com.udhay.echo.feature.chat.domain.usecase

import com.udhay.echo.feature.chat.domain.model.Chat
import com.udhay.echo.feature.chat.domain.repository.ChatHistoryRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class ObserveChatsUseCase(
    private val repository: ChatHistoryRepository
) {
    operator fun invoke(): Flow<List<Chat>> = repository.observeChats()
}
