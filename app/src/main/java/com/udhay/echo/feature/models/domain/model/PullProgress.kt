package com.udhay.echo.feature.models.domain.model

/**
 * A progress update from a streaming pull/push/create operation.
 */
data class PullProgress(
    val status: String? = null,
    val completed: Long = 0,
    val total: Long = 0,
    val error: String? = null
) {
    /** 0f..1f progress, or `null` when the total size is unknown. */
    val fraction: Float?
        get() = if (total > 0) (completed.toFloat() / total).coerceIn(0f, 1f) else null
}
