package com.udhay.echo.feature.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udhay.echo.feature.chat.data.model.toApiMessage
import com.udhay.echo.feature.chat.domain.model.Chat
import com.udhay.echo.feature.chat.domain.model.ChatMessage
import com.udhay.echo.feature.chat.domain.model.ChatMessageMetadata
import com.udhay.echo.feature.chat.domain.usecase.ChatWithModelStreamUseCase
import com.udhay.echo.feature.chat.domain.usecase.CreateChatUseCase
import com.udhay.echo.feature.chat.domain.usecase.GetModelCapabilitiesUseCase
import com.udhay.echo.feature.chat.domain.usecase.DeleteChatUseCase
import com.udhay.echo.feature.chat.domain.usecase.GenerateChatTitleUseCase
import com.udhay.echo.feature.chat.domain.usecase.ObserveChatMessagesUseCase
import com.udhay.echo.feature.chat.domain.usecase.ObserveChatsUseCase
import com.udhay.echo.feature.chat.domain.usecase.SaveChatMessageUseCase
import com.udhay.echo.feature.chat.domain.usecase.TruncateChatFromUseCase
import com.udhay.echo.feature.chat.domain.usecase.UpdateChatTitleUseCase
import com.udhay.echo.feature.chat.domain.model.ToolCallInfo
import com.udhay.echo.feature.chat.presentation.state.ChatUiState
import com.udhay.echo.feature.settings.domain.model.UserSettings
import com.udhay.echo.feature.settings.domain.usecase.GetUserSettingsUseCase
import com.udhay.echo.feature.tools.data.model.toApiTool
import com.udhay.echo.feature.tools.domain.usecase.GetEnabledToolsUseCase
import org.udhay.ollama.api.Tool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import org.koin.core.annotation.KoinViewModel
import org.udhay.ollama.api.ChatRequest
import org.udhay.ollama.api.Message
import org.udhay.ollama.api.MessageRole
import org.udhay.ollama.api.Options
import java.util.UUID

@KoinViewModel
class ChatViewModel(
    private val chatWithModelStreamUseCase: ChatWithModelStreamUseCase,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val observeChatsUseCase: ObserveChatsUseCase,
    private val observeChatMessagesUseCase: ObserveChatMessagesUseCase,
    private val createChatUseCase: CreateChatUseCase,
    private val saveChatMessageUseCase: SaveChatMessageUseCase,
    private val deleteChatUseCase: DeleteChatUseCase,
    private val truncateChatFromUseCase: TruncateChatFromUseCase,
    private val generateChatTitleUseCase: GenerateChatTitleUseCase,
    private val updateChatTitleUseCase: UpdateChatTitleUseCase,
    private val getModelCapabilitiesUseCase: GetModelCapabilitiesUseCase,
    private val getEnabledToolsUseCase: GetEnabledToolsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    val chats = observeChatsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun sendMessage(text: String, images: List<String> = emptyList()) {
        val trimmed = text.trim()
        if ((trimmed.isBlank() && images.isEmpty()) || _uiState.value.isStreaming) return

        viewModelScope.launch {
            val settings = getUserSettingsUseCase().first()
            val isIncognito = _uiState.value.isIncognito

            // Ensure a chat exists (persisted mode only) before attaching messages.
            var chatId = _uiState.value.currentChatId
            var startedNewChat = false
            if (!isIncognito && chatId == null) {
                chatId = UUID.randomUUID().toString()
                startedNewChat = true
                val now = System.currentTimeMillis()
                val title = titleFrom(trimmed.ifBlank { "Image" })
                createChatUseCase(
                    Chat(id = chatId, title = title, createdAt = now, updatedAt = now)
                )
                _uiState.update { it.copy(currentChatId = chatId) }
            }
            val effectiveChatId = chatId ?: INCOGNITO_CHAT_ID

            val userMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                chatId = effectiveChatId,
                role = MessageRole.User,
                content = trimmed,
                images = images.ifEmpty { null },
                createdAt = System.currentTimeMillis()
            )
            _uiState.update { it.copy(messages = it.messages + userMessage, error = null) }
            if (!isIncognito) saveChatMessageUseCase(userMessage)

            val model = settings.selectedModel?.model
            if (model.isNullOrBlank()) {
                _uiState.update { it.copy(error = "No model selected") }
                return@launch
            }

            if (startedNewChat && chatId != null && trimmed.isNotBlank()) {
                generateTitleAsync(chatId, model, trimmed)
            }

            streamAssistantReply(model, settings, effectiveChatId, isIncognito)
        }
    }

    /**
     * Feeds a tool's result back to the model (role = tool) so it can continue the turn.
     * The caller supplies the result manually since tool execution happens outside the app.
     */
    fun submitToolResult(toolName: String, result: String) {
        if (result.isBlank() || _uiState.value.isStreaming) return

        viewModelScope.launch {
            val settings = getUserSettingsUseCase().first()
            val isIncognito = _uiState.value.isIncognito
            val chatId = _uiState.value.currentChatId ?: INCOGNITO_CHAT_ID

            val toolMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                role = MessageRole.Tool,
                content = result.trim(),
                toolName = toolName,
                createdAt = System.currentTimeMillis()
            )
            _uiState.update { it.copy(messages = it.messages + toolMessage, error = null) }
            if (!isIncognito) saveChatMessageUseCase(toolMessage)

            val model = settings.selectedModel?.model
            if (model.isNullOrBlank()) {
                _uiState.update { it.copy(error = "No model selected") }
                return@launch
            }
            streamAssistantReply(model, settings, chatId, isIncognito)
        }
    }

    private suspend fun streamAssistantReply(
        model: String,
        settings: UserSettings,
        chatId: String,
        isIncognito: Boolean,
    ) {
        val assistantId = UUID.randomUUID().toString()
        val placeholder = ChatMessage(
            id = assistantId,
            chatId = chatId,
            role = MessageRole.Assistant,
            content = "",
            isStreaming = true,
            createdAt = System.currentTimeMillis()
        )
        _uiState.update { it.copy(messages = it.messages + placeholder, isStreaming = true) }

        val requestMessages = buildList {
            buildSystemPrompt(settings)?.let { add(Message(role = MessageRole.System, content = it)) }
            addAll(
                _uiState.value.messages
                    .filterNot { it.isStreaming }
                    .map { it.toApiMessage() }
            )
        }

        val capabilities = when {
            settings.selectedModel == null -> emptyList()
            settings.selectedModel.capabilities.isNotEmpty() -> settings.selectedModel.capabilities
            // Legacy selection without cached capabilities — resolve once.
            else -> runCatching { getModelCapabilitiesUseCase(model) }.getOrDefault(emptyList())
        }
        val supportsThinking = settings.thinkingEnabled && capabilities.contains("thinking")
        val tools: List<Tool>? = if (capabilities.contains("tools")) {
            getEnabledToolsUseCase().takeIf { it.isNotEmpty() }?.map { it.toApiTool() }
        } else null

        chatWithModelStreamUseCase(
            ChatRequest(
                model = model,
                messages = requestMessages,
                stream = true,
                tools = tools,
                think = if (supportsThinking) JsonPrimitive(true) else null,
                options = buildOptions(settings),
                keepAlive = settings.keepAlive?.takeIf { it.isNotBlank() }?.let { JsonPrimitive(it) }
            )
        ).catch { e ->
            _uiState.update {
                it.copy(
                    messages = it.messages.filterNot { m -> m.id == assistantId },
                    isStreaming = false,
                    error = e.message ?: "Something went wrong"
                )
            }
        }.collect { response ->
            val chunk = response.message?.content.orEmpty()
            val thinkingChunk = response.thinking.orEmpty()
            val toolCalls = response.message?.toolCalls?.mapNotNull { tc ->
                val name = tc.function?.name ?: return@mapNotNull null
                ToolCallInfo(name = name, arguments = tc.function?.arguments?.toString() ?: "{}")
            }
            _uiState.update { state ->
                state.copy(
                    messages = state.messages.map { m ->
                        if (m.id != assistantId) m
                        else m.copy(
                            content = m.content + chunk,
                            thinking = (m.thinking.orEmpty() + thinkingChunk).ifBlank { null },
                            toolCalls = if (!toolCalls.isNullOrEmpty()) toolCalls else m.toolCalls,
                            metadata = if (response.done == true) response.toMetadata() else m.metadata
                        )
                    }
                )
            }
        }

        finalizeAssistantReply(assistantId, isIncognito)
    }

    private suspend fun finalizeAssistantReply(assistantId: String, isIncognito: Boolean) {
        val finalMessage = _uiState.value.messages.firstOrNull { it.id == assistantId }

        if (finalMessage == null ||
            (finalMessage.content.isBlank() && finalMessage.toolCalls.isNullOrEmpty())
        ) {
            // Repository swallows network failures into an empty stream, so a blank
            // reply here means the request never reached the model.
            _uiState.update {
                it.copy(
                    messages = it.messages.filterNot { m -> m.id == assistantId },
                    isStreaming = false,
                    error = if (it.error != null) it.error
                    else "No response — check that the Ollama server is reachable."
                )
            }
            return
        }

        val settled = finalMessage.copy(isStreaming = false)
        _uiState.update { state ->
            state.copy(
                messages = state.messages.map { if (it.id == assistantId) settled else it },
                isStreaming = false
            )
        }
        if (!isIncognito) saveChatMessageUseCase(settled)
    }

    fun loadChat(chatId: String) {
        viewModelScope.launch {
            val messages = observeChatMessagesUseCase(chatId).first()
            _uiState.update {
                it.copy(
                    currentChatId = chatId,
                    messages = messages,
                    isIncognito = false,
                    isStreaming = false,
                    error = null
                )
            }
        }
    }

    fun newChat() {
        _uiState.update {
            ChatUiState(isIncognito = false)
        }
    }

    fun toggleIncognito() {
        _uiState.update {
            ChatUiState(isIncognito = !it.isIncognito)
        }
    }

    fun deleteChat(chatId: String) {
        viewModelScope.launch {
            deleteChatUseCase(chatId)
            if (_uiState.value.currentChatId == chatId) newChat()
        }
    }

    /**
     * Trims the conversation back to just before [message] so an edited version can be
     * resent. The screen loads [message]'s text into the input; a subsequent send
     * regenerates the reply.
     */
    fun prepareEdit(message: ChatMessage) {
        val index = _uiState.value.messages.indexOfFirst { it.id == message.id }
        if (index < 0) return

        _uiState.update {
            it.copy(messages = it.messages.subList(0, index), isStreaming = false, error = null)
        }
        val chatId = _uiState.value.currentChatId
        if (chatId != null && !_uiState.value.isIncognito) {
            viewModelScope.launch { truncateChatFromUseCase(chatId, message.createdAt) }
        }
    }

    /** Fire-and-forget: replace the provisional truncated title with a model-generated one. */
    private fun generateTitleAsync(chatId: String, model: String, firstMessage: String) {
        viewModelScope.launch {
            val title = runCatching { generateChatTitleUseCase(model, firstMessage) }.getOrNull()
            if (!title.isNullOrBlank()) {
                updateChatTitleUseCase(chatId, title.take(TITLE_MAX_LENGTH))
            }
        }
    }

    private fun buildOptions(settings: UserSettings): Options? {
        val options = Options(
            temperature = settings.temperature?.toDouble(),
            topK = settings.topK,
            topP = settings.topP?.toDouble(),
            numCtx = settings.numCtx,
        )
        return options.takeIf { it != Options() }
    }

    private fun buildSystemPrompt(settings: UserSettings): String? {
        val parts = buildList {
            if (settings.username.isNotBlank()) add("The user's name is ${settings.username}.")
            if (settings.occupation.isNotBlank()) add("The user's occupation is ${settings.occupation}.")
            if (settings.personalPreferences.isNotBlank()) {
                add("Follow these user preferences when replying: ${settings.personalPreferences}")
            }
        }
        return parts.joinToString("\n").ifBlank { null }
    }

    private fun titleFrom(text: String): String {
        val singleLine = text.replace('\n', ' ').trim()
        return if (singleLine.length <= TITLE_MAX_LENGTH) singleLine
        else singleLine.take(TITLE_MAX_LENGTH).trimEnd() + "…"
    }

    private fun org.udhay.ollama.api.ChatResponse.toMetadata() = ChatMessageMetadata(
        model = model,
        totalDuration = totalDuration,
        loadDuration = loadDuration,
        promptEvalCount = promptEvalCount,
        promptEvalDuration = promptEvalDuration,
        evalCount = evalCount,
        evalDuration = evalDuration
    )

    private companion object {
        const val INCOGNITO_CHAT_ID = "incognito"
        const val TITLE_MAX_LENGTH = 40
    }
}
