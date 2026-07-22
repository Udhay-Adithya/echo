package com.udhay.kollama.feature.models.domain.repository

import com.udhay.kollama.feature.chat.domain.model.OllamaModel
import com.udhay.kollama.feature.models.domain.model.ModelDetail
import com.udhay.kollama.feature.models.domain.model.PullProgress
import com.udhay.kollama.feature.models.domain.model.RunningModel
import kotlinx.coroutines.flow.Flow

interface ModelManagementRepository {

    suspend fun listModels(): List<OllamaModel>

    suspend fun runningModels(): List<RunningModel>

    suspend fun showModel(name: String): ModelDetail

    suspend fun deleteModel(name: String): Result<Unit>

    suspend fun copyModel(source: String, destination: String): Result<Unit>

    fun pullModel(name: String): Flow<PullProgress>

    fun pushModel(name: String): Flow<PullProgress>

    fun createModel(name: String, fromModel: String, system: String?): Flow<PullProgress>
}
