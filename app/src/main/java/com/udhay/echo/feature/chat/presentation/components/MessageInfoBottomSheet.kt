package com.udhay.echo.feature.chat.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.udhay.echo.core.utils.formatTimestamp
import com.udhay.echo.feature.chat.domain.model.ChatMessage
import com.udhay.echo.feature.chat.domain.model.ChatMessageMetadata
import org.udhay.ollama.api.MessageRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInfoBottomSheet(
    message: ChatMessage,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Message details",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(16.dp))

            InfoRow("Role", message.role.label())
            InfoRow("Time", formatTimestamp(message.createdAt))
            InfoRow("Characters", message.content.length.toString())

            val metadata = message.metadata
            if (metadata != null) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))

                metadata.model?.let { InfoRow("Model", it) }
                metadata.tokensPerSecond()?.let { InfoRow("Speed", "$it tok/s") }
                metadata.promptEvalCount?.let { InfoRow("Prompt tokens", it.toString()) }
                metadata.evalCount?.let { InfoRow("Response tokens", it.toString()) }
                metadata.totalDuration.nanosToSeconds()?.let { InfoRow("Total time", "$it s") }
                metadata.loadDuration.nanosToSeconds()?.let { InfoRow("Load time", "$it s") }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

private fun MessageRole.label(): String = when (this) {
    MessageRole.System -> "System"
    MessageRole.User -> "You"
    MessageRole.Assistant -> "Assistant"
    MessageRole.Tool -> "Tool"
}

private fun Long?.nanosToSeconds(): String? {
    if (this == null || this <= 0L) return null
    return String.format("%.2f", this / 1_000_000_000.0)
}

private fun ChatMessageMetadata.tokensPerSecond(): String? {
    val count = evalCount ?: return null
    val duration = evalDuration ?: return null
    if (duration <= 0L) return null
    return String.format("%.1f", count / (duration / 1_000_000_000.0))
}
