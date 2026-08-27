package com.udhay.echo.core.di

import com.udhay.echo.feature.settings.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.udhay.ollama.OllamaClient
import org.udhay.ollama.OllamaClientConfig

@Module
class NetworkModule {

    @Single
    fun provideOllamaClient(repository: UserSettingsRepository): OllamaClient {
        return OllamaClient(
            configProvider = {
                val settings = repository.settings.first()
                OllamaClientConfig(
                    host = settings.serverHost,
                    headers = settings.serverHeaders,
                    // No request ceiling: it bounds the response body read too, so any finite
                    // value truncates a long stream mid-flight. socketTimeoutMillis is the right
                    // guard — it measures inactivity, not total duration, so a stalled connection
                    // still fails while a slow-but-alive generation runs to completion.
                    requestTimeoutMillis = null,
                    connectTimeoutMillis = 10_000, // 10 seconds
                    socketTimeoutMillis = 300_000  // 5 minutes of silence
                )
            }
        )
    }
}
