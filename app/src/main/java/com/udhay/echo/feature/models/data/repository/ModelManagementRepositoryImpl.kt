package com.udhay.echo.feature.models.data.repository

import com.udhay.echo.feature.chat.domain.model.OllamaModel
import com.udhay.echo.feature.chat.data.model.toDomain as tagToDomain
import com.udhay.echo.feature.models.data.model.toDomain
import com.udhay.echo.feature.models.domain.model.ModelDetail
import com.udhay.echo.feature.models.domain.model.PullProgress
import com.udhay.echo.feature.models.domain.model.RunningModel
import com.udhay.echo.feature.models.domain.repository.ModelManagementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import org.udhay.ollama.OllamaClient
import org.udhay.ollama.api.CopyRequest
import org.udhay.ollama.api.CreateRequest
import org.udhay.ollama.api.DeleteRequest
import org.udhay.ollama.api.PullRequest
import org.udhay.ollama.api.PushRequest
import org.udhay.ollama.api.ShowRequest

@Single
class ModelManagementRepositoryImpl(
    private val ollamaClient: OllamaClient
) : ModelManagementRepository {

    override suspend fun listModels(): List<OllamaModel> =
        ollamaClient.list().models.map { it.tagToDomain() }

    override suspend fun runningModels(): List<RunningModel> =
        ollamaClient.ps().models.map { it.toDomain() }

    override suspend fun showModel(name: String): ModelDetail =
        ollamaClient.show(ShowRequest(model = name)).toDomain(name)

    override suspend fun deleteModel(name: String): Result<Unit> = runCatching {
        val response = ollamaClient.delete(DeleteRequest(model = name))
        if (response.error != null) error(response.error!!)
    }

    override suspend fun copyModel(source: String, destination: String): Result<Unit> = runCatching {
        val response = ollamaClient.copy(CopyRequest(source = source, destination = destination))
        if (response.error != null) error(response.error!!)
    }

    override fun pullModel(name: String): Flow<PullProgress> =
        ollamaClient.pullStream(PullRequest(model = name, stream = true)).map { it.toDomain() }

    override fun pushModel(name: String): Flow<PullProgress> =
        ollamaClient.pushStream(PushRequest(model = name, stream = true)).map { it.toDomain() }

    override fun createModel(name: String, fromModel: String, system: String?): Flow<PullProgress> =
        ollamaClient.createStream(
            CreateRequest(model = name, fromModel = fromModel, system = system, stream = true)
        ).map { it.toDomain() }
}
