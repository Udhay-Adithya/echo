package com.udhay.echo.feature.chat.domain.repository

import com.udhay.echo.feature.chat.domain.model.OllamaModel

interface ModelsRepository {
    suspend fun getModels() : List<OllamaModel>

    suspend fun getStatus() : Boolean

    suspend fun getModelCapabilities(model: String): List<String>

    suspend fun getRunningModelNames(): Set<String>
}