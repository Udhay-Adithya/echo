package com.udhay.kollama.feature.chat.presentation.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.udhay.kollama.R

/** A staged, not-yet-sent attachment picked via the attach sheet. */
private data class StagedAttachment(val label: String, val iconRes: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputBar(
    textFieldState: TextFieldState,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val hasText = textFieldState.text.isNotEmpty()
    val canSend = enabled && hasText
    val disabledIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)

    var showAttachSheet by remember { mutableStateOf(false) }
    val attachments = remember { mutableStateListOf<StagedAttachment>() }

    val photosLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            attachments.add(StagedAttachment(uri.displayLabel("Image"), R.drawable.photo_24px))
        }
    }
    val filesLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            attachments.add(StagedAttachment(uri.displayLabel("Document"), R.drawable.docs_24px))
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            attachments.add(StagedAttachment("Camera photo", R.drawable.photo_camera_24px))
        }
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column {
            if (attachments.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(start = 12.dp, end = 12.dp, top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    attachments.forEachIndexed { index, attachment ->
                        InputChip(
                            selected = false,
                            onClick = {},
                            label = { Text(attachment.label) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(attachment.iconRes),
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { attachments.removeAt(index) }) {
                                    Icon(
                                        painter = painterResource(R.drawable.close_24px),
                                        contentDescription = "Remove attachment"
                                    )
                                }
                            }
                        )
                    }
                }
            }

            TextField(
                state = textFieldState,
                placeholder = {
                    Text("Message Kollama...")
                },
                modifier = Modifier.fillMaxWidth(),
                lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 6),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send,
                    showKeyboardOnFocus = false
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showAttachSheet = true }, enabled = enabled) {
                    Icon(
                        painter = painterResource(R.drawable.add_24px),
                        contentDescription = "Attach",
                        tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else disabledIconColor
                    )
                }

                ModelSelectorBottomSheet()

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = onSend,
                    enabled = canSend,
                    modifier = Modifier.background(
                        color = if (canSend) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = CircleShape
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.send_24px),
                        contentDescription = "Send",
                        tint = if (canSend) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            disabledIconColor
                        }
                    )
                }
            }
        }
    }

    if (showAttachSheet) {
        AttachmentBottomSheet(
            onCamera = {
                showAttachSheet = false
                cameraLauncher.launch(null)
            },
            onPhotos = {
                showAttachSheet = false
                photosLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onFiles = {
                showAttachSheet = false
                filesLauncher.launch(arrayOf("*/*"))
            },
            onDismiss = { showAttachSheet = false }
        )
    }
}

private fun android.net.Uri.displayLabel(fallback: String): String =
    lastPathSegment?.substringAfterLast('/')?.substringAfterLast(':')?.takeIf { it.isNotBlank() }
        ?: fallback

@Preview(showBackground = true)
@Composable
private fun ChatInputBarPreview() {
    ChatInputBar(
        textFieldState = rememberTextFieldState(),
        onSend = {}
    )
}
