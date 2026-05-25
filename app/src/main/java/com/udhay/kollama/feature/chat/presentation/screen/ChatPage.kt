package com.udhay.kollama.feature.chat.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.udhay.kollama.R
import com.udhay.kollama.core.ui.theme.KollamaTheme
import com.udhay.kollama.feature.chat.presentation.components.ChatBubble
import com.udhay.kollama.feature.chat.presentation.components.ChatDrawer
import com.udhay.kollama.feature.chat.presentation.components.ChatInputBar
import com.udhay.kollama.feature.chat.presentation.components.WelcomeScreen
import com.udhay.kollama.feature.chat.presentation.state.ChatUiState
import com.udhay.kollama.feature.chat.presentation.viewmodel.ChatViewModel
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

    val messages = (uiState as? ChatUiState.Success)?.chatResponses
        ?.mapNotNull { it.message }
        ?.filter { it.content?.isNotBlank() == true } ?: emptyList()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    fun toggleDrawer() {
        scope.launch {
            if (drawerState.isClosed) drawerState.open()
            else drawerState.close()
        }
    }

    fun sendMessage() {
        val text = textFieldState.text.toString().trim()
        if (text.isNotEmpty()) {
            viewModel.sendMessage(text)
            textFieldState.clearText()
        }
    }

    ChatDrawer(
        onOpenSettings = onOpenSettings,
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Kollama",
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
                        IconButton(onClick = {}) {
                            Icon(
                                painter = painterResource(R.drawable.incognito_24px),
                                contentDescription = "Incognito"
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
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (val state = uiState) {
                        is ChatUiState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        is ChatUiState.Error -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = state.message, color = MaterialTheme.colorScheme.error)
                            }
                        }
                        is ChatUiState.Success -> {
                            if (messages.isEmpty()) {
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
                                    itemsIndexed(messages) { _, message ->
                                        ChatBubble(message = message)
                                    }
                                }
                            }
                        }
                    }
                }
                ChatInputBar(
                    textFieldState = textFieldState,
                    onSend = { sendMessage() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .padding(16.dp)

                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatPagePreview() {
    KollamaTheme {
        ChatPage(onOpenSettings = {})
    }
}
