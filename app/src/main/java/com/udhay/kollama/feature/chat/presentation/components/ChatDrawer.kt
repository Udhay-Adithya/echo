package com.udhay.kollama.feature.chat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.udhay.kollama.R
import com.udhay.kollama.core.utils.monthYearLabel
import com.udhay.kollama.feature.chat.domain.model.Chat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDrawer(
    drawerState: DrawerState,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    chats: List<Chat> = emptyList(),
    currentChatId: String? = null,
    onChatClick: (String) -> Unit = {},
    onDeleteChat: (String) -> Unit = {},
    onNewChat: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerShape = RoundedCornerShape(0)) {

                val searchState = rememberTextFieldState()
                val query = searchState.text.toString()
                val results = if (query.isBlank()) emptyList()
                else chats.filter { it.title.contains(query, ignoreCase = true) }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(all = 0.dp)
                        .width(LocalConfiguration.current.screenWidthDp.dp * 0.70f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Kollama",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                        ChatHistorySearchBar(
                            textFieldState = searchState,
                            onSearch = {},
                            searchResults = results.map { it.title },
                            onResultClick = { title ->
                                chats.firstOrNull { it.title == title }?.let { onChatClick(it.id) }
                            }
                        )
                        Spacer(Modifier.height(12.dp))
                        ListItem(
                            headlineContent = { Text("New chat") },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(R.drawable.add_24px),
                                    contentDescription = null
                                )
                            },
                            colors = ListItemDefaults.colors()
                                .copy(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNewChat() }
                        )
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider()
                    }

                    // Chat history list — grouped by month, scrollable, takes remaining space
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (chats.isEmpty()) {
                            Text(
                                "No chats yet",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        chats.groupBy { monthYearLabel(it.updatedAt) }.forEach { (month, monthChats) ->
                            Text(
                                month,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.titleSmall
                            )
                            monthChats.forEach { chat ->
                                ChatHistoryListItem(
                                    title = chat.title,
                                    selected = chat.id == currentChatId,
                                    onClick = { onChatClick(chat.id) },
                                    onDelete = { onDeleteChat(chat.id) }
                                )
                            }
                        }
                    }

                    ListItem(
                        headlineContent = { Text("Settings") },
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.settings_24px),
                                contentDescription = "Settings"
                            )
                        },
                        trailingContent = {
                            Icon(
                                painter = painterResource(R.drawable.more_horiz_24px),
                                contentDescription = "More"
                            )
                        },
                        colors = ListItemDefaults.colors()
                            .copy(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .fillMaxWidth()
                            .clickable { onOpenSettings() }
                    )
                }
            }
        }) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatDrawerPreview() {
    ChatDrawer(
        onOpenSettings = { },
        drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
        chats = listOf(
            Chat(id = "1", title = "Kotlin Jetpack Compose", createdAt = 0, updatedAt = 0),
            Chat(id = "2", title = "Gradient Descent", createdAt = 0, updatedAt = 0)
        )
    )
}
