package com.udhay.echo.feature.tools.domain.repository

import com.udhay.echo.feature.tools.domain.model.ToolDefinition
import kotlinx.coroutines.flow.Flow

interface ToolRepository {

    fun observeTools(): Flow<List<ToolDefinition>>

    suspend fun getEnabledTools(): List<ToolDefinition>

    suspend fun saveTool(tool: ToolDefinition)

    suspend fun setEnabled(id: String, enabled: Boolean)

    suspend fun deleteTool(id: String)
}
