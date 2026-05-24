package com.udhay.kollama.feature.chat.domain.repository

import kotlinx.coroutines.flow.Flow
import org.udhay.ollama.api.ChatRequest
import org.udhay.ollama.api.ChatResponse

interface ChatRepository {
    suspend fun chat(chatRequest: ChatRequest): ChatResponse

    suspend fun chatStream(chatRequest: ChatRequest): Flow<ChatResponse>
}