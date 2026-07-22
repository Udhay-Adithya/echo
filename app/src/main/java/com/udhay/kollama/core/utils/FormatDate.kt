package com.udhay.kollama.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(dateString: String?): String {
    if (dateString == null) return "N/A"

    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

        val date: Date = inputFormat.parse(dateString)!!
        outputFormat.format(date)
    } catch (e: Exception) {
        dateString
    }
}

/** Groups chats in the drawer, e.g. "Jul 2026". */
fun monthYearLabel(millis: Long): String =
    SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date(millis))

/** Full timestamp for the message info sheet, e.g. "22 Jul 2026, 03:14 PM". */
fun formatTimestamp(millis: Long): String =
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(millis))