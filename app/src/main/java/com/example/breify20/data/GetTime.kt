package com.example.breify20.data

import java.text.SimpleDateFormat
import java.util.Locale

fun getGmailTimestamp(dateString: String): Long {
    return try {
        val gmailFormat =
            SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)

        val date = gmailFormat.parse(dateString)
        date?.time ?: 0L

    } catch (e: Exception) {
        0L
    }
}
fun getOutlookTimestamp(dateString: String): Long {
    return try {
        val instant = java.time.Instant.parse(dateString)
        instant.toEpochMilli()

    } catch (e: Exception) {
        0L
    }
}
