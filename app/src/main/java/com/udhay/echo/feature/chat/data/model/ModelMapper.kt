package com.udhay.echo.feature.chat.data.model

import com.udhay.echo.feature.chat.domain.model.OllamaModel
import org.udhay.ollama.api.ModelTag

fun ModelTag.toDomain(): OllamaModel {
    return OllamaModel(
        name = this.name,
        model = this.model ?: "",
        modifiedAt = this.modifiedAt ?: "",
        size = this.size ?: 0L,
        digest = this.digest ?: "",
        details = this.details
    )
}