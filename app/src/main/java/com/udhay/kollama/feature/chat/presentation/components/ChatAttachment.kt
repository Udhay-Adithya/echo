package com.udhay.kollama.feature.chat.presentation.components

/**
 * A staged attachment in the input bar, before a message is sent.
 *
 * @property base64 Raw image bytes as base64 when the attachment is a sendable image,
 *   or `null` for a non-image file kept for display only.
 */
data class ChatAttachment(
    val id: String,
    val label: String,
    val iconRes: Int,
    val base64: String? = null
) {
    val isImage: Boolean get() = base64 != null
}
