package com.udhay.kollama.feature.chat.domain.usecase

import com.udhay.kollama.feature.chat.domain.model.Chat
import com.udhay.kollama.feature.chat.domain.repository.ChatHistoryRepository
import org.koin.core.annotation.Factory

@Factory
class CreateChatUseCase(
    private val repository: ChatHistoryRepository
) {
    suspend operator fun invoke(chat: Chat) = repository.createChat(chat)
}
