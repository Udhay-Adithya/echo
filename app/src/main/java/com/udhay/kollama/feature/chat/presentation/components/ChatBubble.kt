package com.udhay.kollama.feature.chat.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.udhay.kollama.R
import com.udhay.kollama.core.ui.theme.KollamaTheme
import org.udhay.ollama.api.Message
import org.udhay.ollama.api.MessageRole

@Composable
fun ChatBubble(message: Message) {
    val content = message.content ?: return

    val isUser = message.role == MessageRole.User

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

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = arrangement
        ) {
            Surface(
                color = containerColor,
                contentColor = contentColor,
                shape = shape,
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                KollamaMarkdown(
                    content = content,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    contentColor = contentColor
                )
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
                IconButton(onClick = { /* copy */ }) {
                    Icon(
                        painter = painterResource(R.drawable.content_copy_24px),
                        contentDescription = "Copy message"
                    )
                }

                IconButton(onClick = { /* edit */ }) {
                    Icon(
                        painter = painterResource(R.drawable.edit_square_24px),
                        contentDescription = "Edit message"
                    )
                }

                IconButton(onClick = { /* info */ }) {
                    Icon(
                        painter = painterResource(R.drawable.info_24px),
                        contentDescription = "Message info"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatBubbleMarkdownPreview() {
    KollamaTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ChatBubble(
                message = Message(
                    role = MessageRole.User,
                    content = "Hello! **This is bold** and *this is italic*."
                )
            )
            ChatBubble(
                message = Message(
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
