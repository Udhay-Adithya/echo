package com.udhay.kollama.feature.chat.domain.repository

import com.udhay.kollama.feature.chat.domain.model.OllamaModel
import kotlinx.coroutines.flow.Flow
import org.udhay.ollama.api.ChatRequest
import org.udhay.ollama.api.ChatResponse
import org.udhay.ollama.api.ListResponse

interface ModelsRepository {
    suspend fun getModels() : List<OllamaModel>

    suspend fun getStatus() : Boolean
}