package com.udhay.kollama.feature.chat.data.repository

import com.udhay.kollama.feature.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.core.annotation.Single
import org.udhay.ollama.OllamaClient
import org.udhay.ollama.api.ChatRequest
import org.udhay.ollama.api.ChatResponse

@Single
class ChatRepositoryImpl(
    private val ollamaClient: OllamaClient,
) : ChatRepository {
    override suspend fun chat(chatRequest: ChatRequest): ChatResponse {
        return try {
            ollamaClient.chat(chatRequest)
        } catch (e: Exception) {
            ChatResponse()
        }
    }

    override suspend fun chatStream(chatRequest: ChatRequest): Flow<ChatResponse> {
        return try {
            ollamaClient.chatStream(chatRequest)
        } catch (e: Exception) {
            emptyFlow<ChatResponse>()
        }
    }
}