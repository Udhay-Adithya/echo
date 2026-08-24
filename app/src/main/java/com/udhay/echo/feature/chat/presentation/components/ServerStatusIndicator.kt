package com.udhay.echo.feature.chat.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ServerStatusIndicator(
    isOnline: Boolean?,
    modifier: Modifier = Modifier
) {
    val statusColor by animateColorAsState(
        targetValue = when (isOnline) {
            true -> Color.Green
            false -> MaterialTheme.colorScheme.error
            null -> Color.Gray
        },
        label = "statusColor"
    )

    val statusText = when (isOnline) {
        true -> "Connected"
        false -> "Disconnected"
        null -> "Checking status..."
    }

    TextButton(
        onClick = { /* No-op for ripple effect only */ },
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ServerStatusIndicatorOnlinePreview() {
    ServerStatusIndicator(isOnline = true)
}

@Preview(showBackground = true)
@Composable
private fun ServerStatusIndicatorOfflinePreview() {
    ServerStatusIndicator(isOnline = false)
}

@Preview(showBackground = true)
@Composable
private fun ServerStatusIndicatorCheckingPreview() {
    ServerStatusIndicator(isOnline = null)
}
