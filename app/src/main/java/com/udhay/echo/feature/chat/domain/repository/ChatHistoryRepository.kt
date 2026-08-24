package com.udhay.echo.feature.chat.domain.repository

import com.udhay.echo.feature.chat.domain.model.Chat
import com.udhay.echo.feature.chat.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

/**
 * Local persistence for chat history. Separate from the network-facing
 * [ChatRepository] so that talking to the model and storing conversations stay
 * independent concerns.
 */
interface ChatHistoryRepository {

    fun observeChats(): Flow<List<Chat>>

    fun observeMessages(chatId: String): Flow<List<ChatMessage>>

    suspend fun createChat(chat: Chat)

    suspend fun saveMessage(message: ChatMessage)

    suspend fun updateMessage(message: ChatMessage)

    suspend fun touchChat(chatId: String, updatedAt: Long)

    suspend fun updateChatTitle(chatId: String, title: String)

    suspend fun deleteChat(chatId: String)

    /** Deletes every message in [chatId] created at or after [fromCreatedAt] (edit/regenerate). */
    suspend fun truncateFrom(chatId: String, fromCreatedAt: Long)
}
