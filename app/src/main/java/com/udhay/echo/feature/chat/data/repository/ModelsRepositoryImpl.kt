package com.udhay.echo.feature.chat.data.repository

import com.udhay.echo.feature.chat.data.model.toDomain
import com.udhay.echo.feature.chat.domain.model.OllamaModel
import com.udhay.echo.feature.chat.domain.repository.ModelsRepository
import com.udhay.echo.feature.settings.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Single
import org.udhay.ollama.OllamaClient
import org.udhay.ollama.api.ShowRequest

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

    override suspend fun getModelCapabilities(model: String): List<String> {
        return try {
            ollamaClient.show(ShowRequest(model = model)).capabilities ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getRunningModelNames(): Set<String> {
        return try {
            ollamaClient.ps().models.mapNotNull { it.name ?: it.model }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }
}