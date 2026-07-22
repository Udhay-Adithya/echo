package com.udhay.kollama.feature.tools.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolDao {

    @Query("SELECT * FROM tools ORDER BY createdAt ASC")
    fun observeTools(): Flow<List<ToolEntity>>

    @Query("SELECT * FROM tools WHERE enabled = 1")
    suspend fun getEnabledTools(): List<ToolEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTool(tool: ToolEntity)

    @Query("UPDATE tools SET enabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: String, enabled: Boolean)

    @Query("DELETE FROM tools WHERE id = :id")
    suspend fun deleteTool(id: String)
}
