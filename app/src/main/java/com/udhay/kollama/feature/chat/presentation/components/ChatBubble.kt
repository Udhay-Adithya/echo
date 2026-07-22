package com.udhay.kollama.feature.chat.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.udhay.kollama.R
import com.udhay.kollama.core.ui.theme.KollamaTheme
import com.udhay.kollama.core.utils.decodeBase64ToImageBitmap
import com.udhay.kollama.feature.chat.domain.model.ChatMessage
import org.udhay.ollama.api.MessageRole

@Composable
fun ChatBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
    onEdit: (ChatMessage) -> Unit = {}
) {
    val content = message.content
    val images = message.images.orEmpty()
    if (content.isBlank() && images.isEmpty()) return

    val isUser = message.role == MessageRole.User
    val clipboard = LocalClipboardManager.current
    var showInfo by remember { mutableStateOf(false) }

    val arrangement = if (isUser) Arrangement.End else Arrangement.Start

    val shape = if (isUser) {
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
    }

    val containerColor = if (isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest
    }

    val contentColor = if (isUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Column(modifier = modifier) {
        if (!isUser && !message.thinking.isNullOrBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                ThinkingSection(
                    thinking = message.thinking,
                    isStreaming = message.isStreaming,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val bubbleMaxWidth = maxWidth * 0.75f

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = arrangement
            ) {
                Surface(
                    color = containerColor,
                    contentColor = contentColor,
                    shape = shape,
                    modifier = Modifier.widthIn(max = bubbleMaxWidth)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                        images.forEach { data ->
                            BubbleImage(data)
                            Spacer(modifier = Modifier.height(if (content.isBlank()) 0.dp else 8.dp))
                        }
                        if (content.isNotBlank()) {
                            KollamaMarkdown(
                                content = content,
                                contentColor = contentColor
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = arrangement
        ) {
            Row {
                IconButton(onClick = { clipboard.setText(AnnotatedString(content)) }) {
                    Icon(
                        painter = painterResource(R.drawable.content_copy_24px),
                        contentDescription = "Copy message"
                    )
                }

                if (isUser) {
                    IconButton(onClick = { onEdit(message) }) {
                        Icon(
                            painter = painterResource(R.drawable.edit_square_24px),
                            contentDescription = "Edit message"
                        )
                    }
                }

                IconButton(onClick = { showInfo = true }) {
                    Icon(
                        painter = painterResource(R.drawable.info_24px),
                        contentDescription = "Message info"
                    )
                }
            }
        }
    }

    if (showInfo) {
        MessageInfoBottomSheet(
            message = message,
            onDismiss = { showInfo = false }
        )
    }
}

@Composable
private fun BubbleImage(data: String) {
    val bitmap = remember(data) { decodeBase64ToImageBitmap(data) } ?: return
    Image(
        bitmap = bitmap,
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 240.dp)
            .clip(RoundedCornerShape(12.dp))
    )
}

@Preview(showBackground = true)
@Composable
private fun ChatBubbleMarkdownPreview() {
    KollamaTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ChatBubble(
                message = ChatMessage(
                    id = "1",
                    chatId = "c",
                    role = MessageRole.User,
                    content = "Hello! **This is bold** and *this is italic*."
                )
            )
            ChatBubble(
                message = ChatMessage(
                    id = "2",
                    chatId = "c",
                    role = MessageRole.Assistant,
                    content = """
                        Here is some code:
                        ```kotlin
                        fun hello() = println("Hello")
                        ```
                        And a list:
                        - Item 1
                        - Item 2
                    """.trimIndent()
                )
            )
        }
    }
}
