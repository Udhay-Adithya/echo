package com.udhay.echo.feature.chat.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.udhay.echo.R
import com.udhay.echo.core.ui.common.Loader
import com.udhay.echo.core.ui.theme.EchoTheme
import com.udhay.echo.feature.chat.presentation.components.ChatAttachment
import com.udhay.echo.feature.chat.presentation.components.ChatBubble
import com.udhay.echo.feature.chat.presentation.components.ChatDrawer
import com.udhay.echo.feature.chat.presentation.components.ChatInputBar
import com.udhay.echo.feature.chat.presentation.components.WelcomeScreen
import com.udhay.echo.feature.chat.presentation.state.isWaitingForAssistant
import com.udhay.echo.feature.chat.presentation.state.shouldShowAssistantLoader
import com.udhay.echo.feature.chat.presentation.state.visibleMessages
import com.udhay.echo.feature.chat.presentation.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPage(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = koinViewModel(),
    onOpenSettings: () -> Unit,
) {
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val textFieldState: TextFieldState = rememberTextFieldState()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val uiState by viewModel.uiState.collectAsState()
    val chats by viewModel.chats.collectAsStateWithLifecycle()
    val attachments = remember { mutableStateListOf<ChatAttachment>() }

    val visibleMessages = uiState.visibleMessages
    val showLoaderBubble = uiState.shouldShowAssistantLoader
    val hasError = uiState.error != null
    val visibleItemCount = visibleMessages.size +
            (if (showLoaderBubble) 1 else 0) +
            (if (hasError) 1 else 0)

    LaunchedEffect(visibleItemCount) {
        if (visibleItemCount > 0) {
            listState.animateScrollToItem(visibleItemCount - 1)
        }
    }

    fun closeDrawer() = scope.launch { drawerState.close() }

    fun toggleDrawer() {
        scope.launch {
            if (drawerState.isClosed) drawerState.open()
            else drawerState.close()
        }
    }

    fun sendMessage() {
        val text = textFieldState.text.toString().trim()
        val images = attachments.mapNotNull { it.base64 }
        if (text.isNotEmpty() || images.isNotEmpty()) {
            viewModel.sendMessage(text, images)
            textFieldState.clearText()
            attachments.clear()
        }
    }

    ChatDrawer(
        drawerState = drawerState,
        onOpenSettings = onOpenSettings,
        chats = chats,
        currentChatId = uiState.currentChatId,
        onChatClick = { chatId ->
            viewModel.loadChat(chatId)
            closeDrawer()
        },
        onDeleteChat = { chatId -> viewModel.deleteChat(chatId) },
        onNewChat = {
            viewModel.newChat()
            closeDrawer()
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                        title = {
                        Text(
                            text = if (uiState.isIncognito) "Incognito" else "Echo",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { toggleDrawer() }) {
                            Icon(
                                painter = painterResource(R.drawable.menu_24px),
                                contentDescription = "Menu"
                            )
                        }
                    },
                    actions = {
                        val incognitoActive = uiState.isIncognito
                        IconButton(
                            onClick = { viewModel.toggleIncognito() },
                            modifier = Modifier.background(
                                color = if (incognitoActive) MaterialTheme.colorScheme.primary
                                else Color.Transparent,
                                shape = CircleShape
                            )
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.incognito_24px),
                                contentDescription = "Incognito",
                                tint = if (incognitoActive) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            },
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
            ) {
                if (uiState.isIncognito) {
                    IncognitoBanner(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (visibleMessages.isEmpty() && !showLoaderBubble && !hasError) {
                        WelcomeScreen(
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            itemsIndexed(
                                items = visibleMessages,
                                key = { _, message -> message.id }
                            ) { _, message ->
                                ChatBubble(
                                    message = message,
                                    onEdit = { edited ->
                                        textFieldState.setTextAndPlaceCursorAtEnd(edited.content)
                                        viewModel.prepareEdit(edited)
                                    },
                                    onSubmitToolResult = { toolName, result ->
                                        viewModel.submitToolResult(toolName, result)
                                    }
                                )
                            }

                            if (showLoaderBubble) {
                                item {
                                    Loader(
                                        fill = false,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                    )
                                }
                            }

                            uiState.error?.let { error ->
                                item {
                                    ErrorBubble(message = error)
                                }
                            }
                        }
                    }
                }
                ChatInputBar(
                    textFieldState = textFieldState,
                    onSend = { sendMessage() },
                    enabled = !uiState.isWaitingForAssistant,
                    attachments = attachments,
                    onAddAttachment = { attachments.add(it) },
                    onRemoveAttachment = { id -> attachments.removeAll { it.id == id } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun IncognitoBanner(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.incognito_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Incognito mode — this chat is temporary and won't be saved.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

@Composable
private fun ErrorBubble(message: String) {
    Surface(
        shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatPagePreview() {
    EchoTheme {
        ChatPage(onOpenSettings = {})
    }
}
