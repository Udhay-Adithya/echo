package com.udhay.kollama.feature.chat.domain.usecase

import com.udhay.kollama.feature.chat.domain.repository.ChatRepository
import org.koin.core.annotation.Factory
import org.udhay.ollama.api.ChatRequest
import org.udhay.ollama.api.ChatResponse

@Factory
class ChatWithModelUserCase(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(chatRequest: ChatRequest): ChatResponse {
        return chatRepository.chat(chatRequest)
    }
}