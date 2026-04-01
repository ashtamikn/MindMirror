package com.mindmirror.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val GOOGLE_KEEP_PACKAGE = "com.google.android.keep"

fun shareEntryBackup(
    context: Context,
    dateMillis: Long,
    mood: String,
    content: String
) {
    val backupText = buildEntryBackupText(dateMillis, mood, content)
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "MindMirror entry - ${formatBackupDate(dateMillis)}")
        putExtra(Intent.EXTRA_TEXT, backupText)
    }

    val keepIntent = Intent(sendIntent).apply {
        `package` = GOOGLE_KEEP_PACKAGE
    }

    val launchIntent = if (canOpenKeep(context.packageManager, keepIntent)) {
        keepIntent
    } else {
        Intent.createChooser(sendIntent, "Save diary backup")
    }

    if (context !is Activity) {
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(launchIntent)
}

private fun canOpenKeep(packageManager: PackageManager, intent: Intent): Boolean {
    return intent.resolveActivity(packageManager) != null
}

private fun buildEntryBackupText(
    dateMillis: Long,
    mood: String,
    content: String
): String {
    val trimmedMood = mood.trim()
    val trimmedContent = content.trim()

    return buildString {
        appendLine("Dear Diary")
        appendLine("Date: ${formatBackupDate(dateMillis)}")
        appendLine("Time: ${formatBackupTime(dateMillis)}")
        if (trimmedMood.isNotBlank()) {
            appendLine("Mood: $trimmedMood")
        }
        appendLine()
        appendLine(trimmedContent)
        appendLine()
        append("— Saved from MindMirror")
    }
}

private fun formatBackupDate(epochMillis: Long): String {
    return SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(epochMillis))
}

private fun formatBackupTime(epochMillis: Long): String {
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(epochMillis))
}

