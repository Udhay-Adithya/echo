package com.udhay.echo.feature.chat.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.udhay.echo.core.ui.common.AppTextField
import com.udhay.echo.feature.chat.domain.model.ToolCallInfo

/**
 * Renders a tool call the model requested, with a field to supply the result manually.
 * Actual tool execution happens outside the app; the entered result is fed back as a
 * `role = tool` message so the model can finish its answer.
 */
@Composable
fun ToolCallCard(
    toolCall: ToolCallInfo,
    onSubmitResult: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var result by remember(toolCall) { mutableStateOf("") }
    var submitted by remember(toolCall) { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Tool call · ${toolCall.name}",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    Text(
                        text = toolCall.arguments,
                        style = MaterialTheme.typography.bodySmall
                            .copy(fontFamily = FontFamily.Monospace),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (submitted) {
                Text(
                    text = "Result sent to the model.",
                    style = MaterialTheme.typography.labelMedium
                )
            } else {
                AppTextField(
                    value = result,
                    onValueChange = { result = it },
                    placeholder = "Enter this tool's result to continue",
                    singleLine = false,
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        onSubmitResult(result)
                        submitted = true
                    },
                    enabled = result.isNotBlank()
                ) {
                    Text("Send result")
                }
            }
        }
    }
}
