package com.udhay.echo.feature.chat.domain.usecase

import com.udhay.echo.feature.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory
import org.udhay.ollama.api.ChatRequest
import org.udhay.ollama.api.ChatResponse

@Factory
class ChatWithModelStreamUseCase(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(chatRequest: ChatRequest): Flow<ChatResponse> {
        return chatRepository.chatStream(chatRequest)
    }
}
