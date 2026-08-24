package com.udhay.echo.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/** Reads a content [uri] and returns its raw bytes as a base64 string (Ollama image format). */
suspend fun encodeUriToBase64(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
    runCatching {
        context.contentResolver.openInputStream(uri)?.use { input ->
            Base64.encodeToString(input.readBytes(), Base64.NO_WRAP)
        }
    }.getOrNull()
}

/** Compresses a camera [bitmap] to JPEG and returns it as a base64 string. */
fun encodeBitmapToBase64(bitmap: Bitmap): String {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
    return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
}

/** Decodes a base64 image string for display, or `null` if it cannot be decoded. */
fun decodeBase64ToImageBitmap(data: String): ImageBitmap? = runCatching {
    val bytes = Base64.decode(data, Base64.NO_WRAP)
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
}.getOrNull()

/** True when a content [uri] points at an image the model can consume. */
fun isImageUri(context: Context, uri: Uri): Boolean =
    context.contentResolver.getType(uri)?.startsWith("image/") == true
