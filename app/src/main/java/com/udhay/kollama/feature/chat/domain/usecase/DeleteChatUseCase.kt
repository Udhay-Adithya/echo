package com.udhay.kollama.feature.chat.domain.usecase

import com.udhay.kollama.feature.chat.domain.repository.ChatHistoryRepository
import org.koin.core.annotation.Factory

@Factory
class DeleteChatUseCase(
    private val repository: ChatHistoryRepository
) {
    suspend operator fun invoke(chatId: String) = repository.deleteChat(chatId)
}
