package com.udhay.echo.feature.chat.domain.usecase

import com.udhay.echo.feature.chat.domain.repository.ChatHistoryRepository
import org.koin.core.annotation.Factory

@Factory
class TruncateChatFromUseCase(
    private val repository: ChatHistoryRepository
) {
    /** Removes the message at [fromCreatedAt] and every later message (edit/regenerate). */
    suspend operator fun invoke(chatId: String, fromCreatedAt: Long) =
        repository.truncateFrom(chatId, fromCreatedAt)
}
