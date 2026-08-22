package com.udhay.echo.feature.chat.domain.usecase

import com.udhay.echo.feature.chat.domain.model.Chat
import com.udhay.echo.feature.chat.domain.repository.ChatHistoryRepository
import org.koin.core.annotation.Factory

@Factory
class CreateChatUseCase(
    private val repository: ChatHistoryRepository
) {
    suspend operator fun invoke(chat: Chat) = repository.createChat(chat)
}
