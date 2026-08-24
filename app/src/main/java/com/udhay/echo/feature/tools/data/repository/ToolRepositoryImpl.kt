package com.udhay.echo.feature.tools.data.repository

import com.udhay.echo.feature.tools.data.local.ToolDao
import com.udhay.echo.feature.tools.data.model.toDomain
import com.udhay.echo.feature.tools.data.model.toEntity
import com.udhay.echo.feature.tools.domain.model.ToolDefinition
import com.udhay.echo.feature.tools.domain.repository.ToolRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class ToolRepositoryImpl(
    private val dao: ToolDao
) : ToolRepository {

    override fun observeTools(): Flow<List<ToolDefinition>> =
        dao.observeTools().map { list -> list.map { it.toDomain() } }

    override suspend fun getEnabledTools(): List<ToolDefinition> =
        dao.getEnabledTools().map { it.toDomain() }

    override suspend fun saveTool(tool: ToolDefinition) = dao.upsertTool(tool.toEntity())

    override suspend fun setEnabled(id: String, enabled: Boolean) = dao.setEnabled(id, enabled)

    override suspend fun deleteTool(id: String) = dao.deleteTool(id)
}
