package com.udhay.echo.core.utils

import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

fun formatFileSize(size: Long?): String {
    if (size == null) return "N/A"

    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()

    return String.format(
        Locale.getDefault(),
        "%.1f %s",
        size / 1024.0.pow(digitGroups.toDouble()),
        units[digitGroups]
    )
}