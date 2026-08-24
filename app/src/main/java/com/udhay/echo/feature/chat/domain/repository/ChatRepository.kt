package com.udhay.echo.feature.chat.domain.repository

import kotlinx.coroutines.flow.Flow
import org.udhay.ollama.api.ChatRequest
import org.udhay.ollama.api.ChatResponse
import org.udhay.ollama.api.GenerateRequest
import org.udhay.ollama.api.GenerateResponse

interface ChatRepository {
    suspend fun chat(chatRequest: ChatRequest): ChatResponse

    suspend fun chatStream(chatRequest: ChatRequest): Flow<ChatResponse>

    suspend fun generate(request: GenerateRequest): GenerateResponse
}