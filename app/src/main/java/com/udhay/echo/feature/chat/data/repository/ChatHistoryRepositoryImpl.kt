package com.udhay.echo.feature.chat.data.repository

import com.udhay.echo.feature.chat.data.local.ChatDao
import com.udhay.echo.feature.chat.data.model.toDomain
import com.udhay.echo.feature.chat.data.model.toEntity
import com.udhay.echo.feature.chat.domain.model.Chat
import com.udhay.echo.feature.chat.domain.model.ChatMessage
import com.udhay.echo.feature.chat.domain.repository.ChatHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class ChatHistoryRepositoryImpl(
    private val dao: ChatDao
) : ChatHistoryRepository {

    override fun observeChats(): Flow<List<Chat>> =
        dao.observeChats().map { list -> list.map { it.toDomain() } }

    override fun observeMessages(chatId: String): Flow<List<ChatMessage>> =
        dao.observeMessages(chatId).map { list -> list.map { it.toDomain() } }

    override suspend fun createChat(chat: Chat) {
        dao.upsertChat(chat.toEntity())
    }

    override suspend fun saveMessage(message: ChatMessage) {
        dao.insertMessage(message.toEntity())
    }

    override suspend fun updateMessage(message: ChatMessage) {
        dao.updateMessage(message.toEntity())
    }

    override suspend fun touchChat(chatId: String, updatedAt: Long) {
        dao.touchChat(chatId, updatedAt)
    }

    override suspend fun updateChatTitle(chatId: String, title: String) {
        dao.updateChatTitle(chatId, title)
    }

    override suspend fun deleteChat(chatId: String) {
        dao.deleteChat(chatId)
    }

    override suspend fun truncateFrom(chatId: String, fromCreatedAt: Long) {
        dao.deleteMessagesFrom(chatId, fromCreatedAt)
    }
}
