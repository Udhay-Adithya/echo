package com.udhay.echo.feature.chat.domain.usecase

import com.udhay.echo.feature.chat.domain.model.ChatMessage
import com.udhay.echo.feature.chat.domain.repository.ChatHistoryRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class ObserveChatMessagesUseCase(
    private val repository: ChatHistoryRepository
) {
    operator fun invoke(chatId: String): Flow<List<ChatMessage>> =
        repository.observeMessages(chatId)
}
