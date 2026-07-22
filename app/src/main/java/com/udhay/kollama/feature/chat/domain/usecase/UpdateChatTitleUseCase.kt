package com.udhay.kollama.feature.chat.domain.usecase

import com.udhay.kollama.feature.chat.domain.repository.ChatHistoryRepository
import org.koin.core.annotation.Factory

@Factory
class UpdateChatTitleUseCase(
    private val repository: ChatHistoryRepository
) {
    suspend operator fun invoke(chatId: String, title: String) =
        repository.updateChatTitle(chatId, title)
}
