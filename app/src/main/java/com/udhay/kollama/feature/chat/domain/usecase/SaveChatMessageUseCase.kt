package com.udhay.kollama.feature.chat.domain.usecase

import com.udhay.kollama.feature.chat.domain.model.ChatMessage
import com.udhay.kollama.feature.chat.domain.repository.ChatHistoryRepository
import org.koin.core.annotation.Factory

@Factory
class SaveChatMessageUseCase(
    private val repository: ChatHistoryRepository
) {
    /** Persists [message] and bumps the parent chat's `updatedAt` to its creation time. */
    suspend operator fun invoke(message: ChatMessage) {
        repository.saveMessage(message)
        repository.touchChat(message.chatId, message.createdAt)
    }
}
