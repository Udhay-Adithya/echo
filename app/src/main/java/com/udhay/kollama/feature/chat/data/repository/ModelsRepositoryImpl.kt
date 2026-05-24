package com.udhay.kollama.feature.chat.data.repository

import com.udhay.kollama.feature.chat.data.model.toDomain
import com.udhay.kollama.feature.chat.domain.model.OllamaModel
import com.udhay.kollama.feature.chat.domain.repository.ModelsRepository
import com.udhay.kollama.feature.settings.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Single
import org.udhay.ollama.OllamaClient
import org.udhay.ollama.api.ChatRequest
import org.udhay.ollama.api.ChatResponse

@Single
class ModelsRepositoryImpl(
    private val ollamaClient: OllamaClient,
    private val settingsRepository: UserSettingsRepository
) : ModelsRepository {
    private suspend fun currentHost(): String =
        settingsRepository.settings.first().serverHost

    override suspend fun getModels(): List<OllamaModel> {
        return try {
            val response = ollamaClient.list()
            response.models.map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getStatus(): Boolean {
        return try {
            ollamaClient.ping()
        } catch (e: Exception) {
            false
        }
    }
}