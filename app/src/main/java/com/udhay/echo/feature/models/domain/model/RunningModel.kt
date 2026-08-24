package com.udhay.echo.feature.models.domain.model

/**
 * A model currently loaded in memory, from `GET /api/ps`.
 */
data class RunningModel(
    val name: String,
    val size: Long? = null,
    val sizeVram: Long? = null,
    val expiresAt: String? = null,
    val contextLength: Int? = null
)
