package com.mindmirror.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun BookLibraryScreen(
    viewModelFactory: ViewModelProvider.Factory,
    onBackToHome: () -> Unit,
    onSelectDate: (Long) -> Unit
) {
    val viewModel: DiaryViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.uiState.collectAsState()

    // Date picker state
    var visibleMonthMillis by remember { mutableLongStateOf(startOfMonthMillis(System.currentTimeMillis())) }
    var showDatePicker by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFAF7F2)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Back button with background
                    Surface(
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp),
                        color = Color(0xFF8B6F47),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        IconButton(onClick = onBackToHome, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Home",
                                tint = Color.White,
                                modifier = Modifier.width(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Library",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3D3D3D)
                    )
                }
            }

            // Date Navigator Section
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Jump to Date",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3D3D3D)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                visibleMonthMillis = shiftMonth(visibleMonthMillis, -1)
                            },
                            modifier = Modifier.width(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowLeft,
                                contentDescription = "Previous Month",
                                tint = Color(0xFF8B6F47)
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clickable { showDatePicker = !showDatePicker },
                            color = Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = formatMonthYear(visibleMonthMillis),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3D3D3D)
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                visibleMonthMillis = shiftMonth(visibleMonthMillis, 1)
                            },
                            modifier = Modifier.width(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "Next Month",
                                tint = Color(0xFF8B6F47)
                            )
                        }
                    }

                    // Calendar Grid
                    if (showDatePicker) {
                        CalendarGrid(
                            monthStartMillis = visibleMonthMillis,
                            onDateSelected = { dateMillis ->
                                onSelectDate(dateMillis)
                                showDatePicker = false
                            },
                            entries = state.entries
                        )
                    }
                }
            }

            // Entries List
            if (state.entries.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "📖",
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No pages yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B6B6B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Start writing your first entry",
                        fontSize = 14.sp,
                        color = Color(0xFF9A9A9A)
                    )
                }
            } else {
                // List of entries
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        state.entries.sortedByDescending { it.createdAtEpochMillis },
                        key = { it.id }
                    ) { entry ->
                        BookPageCard(
                            entry = entry,
                            onClick = { onSelectDate(entry.createdAtEpochMillis) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarGrid(
    monthStartMillis: Long,
    onDateSelected: (Long) -> Unit,
    entries: List<com.mindmirror.data.DiaryEntry>
) {
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B6F47),
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
            }
        }

        // Calendar days
        val firstDayOfMonth = Calendar.getInstance().apply {
            timeInMillis = monthStartMillis
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val year = firstDayOfMonth.get(Calendar.YEAR)
        val month = firstDayOfMonth.get(Calendar.MONTH)
        val firstDayWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = firstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)

        var dayCounter = 1
        repeat(6) { weekIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(7) { dayOfWeek ->
                    if (weekIndex == 0 && dayOfWeek < firstDayWeek) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else if (dayCounter <= daysInMonth) {
                        val dayDate = Calendar.getInstance().apply {
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, month)
                            set(Calendar.DAY_OF_MONTH, dayCounter)
                            set(Calendar.HOUR_OF_DAY, 12)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        
                        val hasEntry = entries.any { 
                            isSameDay(it.createdAtEpochMillis, dayDate.timeInMillis)
                        }
                        
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .padding(2.dp)
                                .clickable { onDateSelected(dayDate.timeInMillis) },
                            color = if (hasEntry) Color(0xFF7CB587) else Color(0xFFEEEEEE),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = dayCounter.toString(),
                                fontSize = 12.sp,
                                fontWeight = if (hasEntry) FontWeight.Bold else FontWeight.Normal,
                                color = if (hasEntry) Color.White else Color(0xFF3D3D3D),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp),
                                maxLines = 1
                            )
                        }
                        dayCounter++
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun BookPageCard(
    entry: com.mindmirror.data.DiaryEntry,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color(0xFF3D3D3D)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Date
            Text(
                text = formatDate(entry.createdAtEpochMillis),
                fontSize = 12.sp,
                color = Color(0xFF8B6F47),
                fontWeight = FontWeight.Bold
            )

            // Mood and Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (entry.mood.isNotBlank()) "Mood: ${entry.mood}" else "Mood: unspecified",
                    fontSize = 13.sp,
                    color = Color(0xFF6B6B6B),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatTime(entry.createdAtEpochMillis),
                    fontSize = 11.sp,
                    color = Color(0xFF9A9A9A)
                )
            }

            // Preview
            Text(
                text = entry.content.take(120) + if (entry.content.length > 120) "..." else "",
                fontSize = 13.sp,
                color = Color(0xFF6B6B6B),
                maxLines = 2
            )
        }
    }
}

private fun formatDate(epochMillis: Long): String {
    val format = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
    return format.format(Date(epochMillis))
}

private fun formatTime(epochMillis: Long): String {
    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return format.format(Date(epochMillis))
}

private fun formatMonthYear(epochMillis: Long): String {
    val format = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    return format.format(Date(epochMillis))
}

private fun isSameDay(millis1: Long, millis2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = millis1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = millis2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}

private fun startOfMonthMillis(sourceMillis: Long): Long {
    return Calendar.getInstance().apply {
        timeInMillis = sourceMillis
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 12)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun shiftMonth(sourceMillis: Long, deltaMonths: Int): Long {
    return Calendar.getInstance().apply {
        timeInMillis = sourceMillis
        add(Calendar.MONTH, deltaMonths)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 12)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

