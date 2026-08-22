package com.udhay.echo.feature.models.domain.model

/**
 * Rich metadata for a single model, from `POST /api/show`.
 */
data class ModelDetail(
    val name: String,
    val license: String? = null,
    val modelfile: String? = null,
    val template: String? = null,
    val system: String? = null,
    val parameters: String? = null,
    val capabilities: List<String> = emptyList(),
    val family: String? = null,
    val parameterSize: String? = null,
    val quantizationLevel: String? = null
)
