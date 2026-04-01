package com.mindmirror.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EntryReaderScreen(
    entry: com.mindmirror.data.DiaryEntry,
    aiEmotion: String,
    aiSummary: String,
    aiGuidance: List<String>,
    aiLoading: Boolean,
    aiError: String?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onSummarize: () -> Unit
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFAF7F2)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back button with background
                Surface(
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp),
                    color = Color(0xFF8B6F47),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Library",
                            tint = Color.White
                        )
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Reading",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3D3D3D),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 12.dp)
                    )
                }

                Surface(
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp),
                    color = Color(0xFF8B6F47),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    IconButton(onClick = onEdit, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Entry",
                            tint = Color.White
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                color = Color(0xFFFFFFFF),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Thought Summary",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3D3D3D)
                    )

                    Button(
                        onClick = onSummarize,
                        enabled = !aiLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7CB587))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Summarize",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (aiLoading) "Analyzing..." else "Summarize my thoughts",
                            color = Color.White
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            shareEntryBackup(
                                context = context,
                                dateMillis = entry.createdAtEpochMillis,
                                mood = entry.mood,
                                content = entry.content
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Save to Google Keep",
                            color = Color(0xFF8B6F47),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (aiLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }

                    if (!aiError.isNullOrBlank()) {
                        Text(
                            text = aiError,
                            color = Color(0xFFB00020),
                            fontSize = 13.sp
                        )
                    }

                    if (aiEmotion.isNotBlank()) {
                        Text(
                            text = "Emotion: $aiEmotion",
                            fontSize = 13.sp,
                            color = Color(0xFF8B6F47),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (aiSummary.isNotBlank()) {
                        Text(
                            text = aiSummary,
                            fontSize = 14.sp,
                            color = Color(0xFF3D3D3D)
                        )
                    }

                    aiGuidance.forEach {
                        Text(
                            text = "- $it",
                            fontSize = 13.sp,
                            color = Color(0xFF6B6B6B)
                        )
                    }
                }
            }

            // Book-like content area
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Date and time
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = formatFullDate(entry.createdAtEpochMillis),
                            fontSize = 14.sp,
                            color = Color(0xFF8B6F47),
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        )
                        Text(
                            text = formatFullTime(entry.createdAtEpochMillis),
                            fontSize = 11.sp,
                            color = Color(0xFF9A9A9A)
                        )
                    }

                    // Divider
                    Text(
                        text = "✦",
                        fontSize = 16.sp,
                        color = Color(0xFFD4A574),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    // Mood if available
                    if (entry.mood.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Mood: ",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF6B6B6B)
                            )
                            Text(
                                text = entry.mood,
                                fontSize = 13.sp,
                                color = Color(0xFF8B6F47),
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Divider
                    Text(
                        text = "✦",
                        fontSize = 16.sp,
                        color = Color(0xFFD4A574),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Content
                    Text(
                        text = entry.content,
                        fontSize = 15.sp,
                        color = Color(0xFF3D3D3D),
                        lineHeight = 24.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Footer divider
                    Text(
                        text = "✦",
                        fontSize = 16.sp,
                        color = Color(0xFFD4A574),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

private fun formatFullDate(epochMillis: Long): String {
    val format = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
    return format.format(Date(epochMillis))
}

private fun formatFullTime(epochMillis: Long): String {
    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return format.format(Date(epochMillis))
}

