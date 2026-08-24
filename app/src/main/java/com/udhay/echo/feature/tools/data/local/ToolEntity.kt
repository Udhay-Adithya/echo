package com.udhay.echo.feature.tools.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tools")
data class ToolEntity(

    @PrimaryKey
    val id: String,

    val name: String,
    val description: String,
    val parameters: String,
    val enabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
