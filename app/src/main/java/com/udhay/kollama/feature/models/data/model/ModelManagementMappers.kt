package com.udhay.kollama.feature.models.data.model

import com.udhay.kollama.feature.models.domain.model.ModelDetail
import com.udhay.kollama.feature.models.domain.model.PullProgress
import com.udhay.kollama.feature.models.domain.model.RunningModel
import org.udhay.ollama.api.ProcessModel
import org.udhay.ollama.api.ProgressResponse
import org.udhay.ollama.api.ShowResponse

fun ShowResponse.toDomain(name: String) = ModelDetail(
    name = name,
    license = license,
    modelfile = modelfile,
    template = template,
    system = system,
    parameters = parameters,
    capabilities = capabilities ?: emptyList(),
    family = details?.family,
    parameterSize = details?.parameterSize,
    quantizationLevel = details?.quantizationLevel
)

fun ProcessModel.toDomain() = RunningModel(
    name = name ?: model ?: "unknown",
    size = size,
    sizeVram = sizeVram,
    expiresAt = expiresAt,
    contextLength = contextLength
)

fun ProgressResponse.toDomain() = PullProgress(
    status = status,
    completed = completed ?: 0,
    total = total ?: 0,
    error = error
)
