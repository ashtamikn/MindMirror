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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun NewEntryScreen(
    viewModelFactory: ViewModelProvider.Factory,
    selectedDateMillis: Long? = null,
    entryToEdit: com.mindmirror.data.DiaryEntry? = null,
    isEditMode: Boolean = false,
    onBack: () -> Unit,
    onEntrySaved: (Long) -> Unit
) {
    val viewModel: DiaryViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var contentText by remember(entryToEdit?.id) { mutableStateOf(entryToEdit?.content ?: "") }
    var moodText by remember(entryToEdit?.id) { mutableStateOf(entryToEdit?.mood ?: "") }
    var selectedDate by remember(entryToEdit?.id, selectedDateMillis) {
        mutableStateOf(entryToEdit?.createdAtEpochMillis ?: selectedDateMillis ?: System.currentTimeMillis())
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var visibleMonthMillis by remember(entryToEdit?.id, selectedDateMillis) {
        mutableLongStateOf(startOfMonthMillis(selectedDate))
    }

    LaunchedEffect(entryToEdit?.id, selectedDateMillis, isEditMode) {
        viewModel.clearReflection()
        viewModel.resetCompanionChat()
        if (contentText.isNotBlank()) {
            viewModel.onWritingDraftChanged(contentText, moodText)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFAF7F2)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
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
                            contentDescription = "Go Back",
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = if (isEditMode) "Edit Page" else "New Page",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3D3D3D)
                    )
                    Text(
                        text = formatDateOnly(selectedDate),
                        fontSize = 12.sp,
                        color = Color(0xFF8B6F47),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Write freely, then save a backup to Google Keep if you want.",
                        fontSize = 12.sp,
                        color = Color(0xFF7A6B57)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = !showDatePicker },
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Page Date",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF8B6F47)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = formatDateOnly(selectedDate),
                                fontSize = 16.sp,
                                color = Color(0xFF3D3D3D),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (showDatePicker) "🔽" else "📅",
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .width(40.dp)
                                    .padding(8.dp)
                            )
                        }
                        Text(
                            text = "Tap to choose today, a past date, or a future page.",
                            fontSize = 12.sp,
                            color = Color(0xFF7A6B57)
                        )
                    }
                }

                // Calendar picker
                if (showDatePicker) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Month/Year Navigation
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                            IconButton(
                                onClick = {
                                    visibleMonthMillis = shiftMonth(visibleMonthMillis, -1)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Previous Month",
                                    tint = Color(0xFF8B6F47)
                                )
                            }
                                
                                Text(
                                    text = formatMonthYear(visibleMonthMillis),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3D3D3D),
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1
                                )
                                
                                IconButton(
                                    onClick = {
                                        visibleMonthMillis = shiftMonth(visibleMonthMillis, 1)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "Next Month",
                                        tint = Color(0xFF8B6F47)
                                    )
                                }
                            }

                            // Day grid
                            DateGridView(
                                monthStartMillis = visibleMonthMillis,
                                selectedDate = selectedDate,
                                onDateSelected = { dateMillis ->
                                    selectedDate = dateMillis
                                    visibleMonthMillis = startOfMonthMillis(dateMillis)
                                    showDatePicker = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Your Page",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3D3D3D)
                        )
                        Text(
                            text = "Start with what happened, how it felt, or what you want to remember.",
                            fontSize = 12.sp,
                            color = Color(0xFF7A6B57)
                        )

                        OutlinedTextField(
                            value = contentText,
                            onValueChange = {
                                contentText = it
                                viewModel.onWritingDraftChanged(contentText, moodText)
                            },
                            placeholder = { Text("Write your thoughts and reflections...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp),
                            minLines = 9,
                            maxLines = 12,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B6F47),
                                unfocusedBorderColor = Color(0xFFD0D0D0),
                                focusedContainerColor = Color(0xFFFFFCF8),
                                unfocusedContainerColor = Color(0xFFFFFCF8)
                            )
                        )

                        Text(
                            text = "Mood (optional)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF3D3D3D)
                        )

                        OutlinedTextField(
                            value = moodText,
                            onValueChange = {
                                moodText = it
                                viewModel.onWritingDraftChanged(contentText, moodText)
                            },
                            placeholder = { Text("e.g., happy, thoughtful, calm...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B6F47),
                                unfocusedBorderColor = Color(0xFFD0D0D0),
                                focusedContainerColor = Color(0xFFFFFCF8),
                                unfocusedContainerColor = Color(0xFFFFFCF8)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Companion Corner",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3D3D3D)
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.chatMessages.takeLast(6).forEach { message ->
                            ChatBubble(message = message)
                        }

                        if (state.isChatTyping) {
                            Surface(
                                color = Color(0xFFF2E9DC),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "typing...",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    color = Color(0xFF6B6B6B),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Reflection",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3D3D3D)
                )

                Button(
                    onClick = { viewModel.generateReflectionForText(contentText, moodText) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = contentText.isNotBlank() && !state.aiLoading,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4A574))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Summarize and guide",
                        tint = Color(0xFF3D3D3D)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.aiLoading) "Analyzing..." else "Summarize my thoughts",
                        color = Color(0xFF3D3D3D),
                        fontWeight = FontWeight.Bold
                    )
                }

                if (state.aiLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                if (!state.aiError.isNullOrBlank()) {
                    Text(
                        text = state.aiError ?: "",
                        fontSize = 13.sp,
                        color = Color(0xFFB00020)
                    )
                }

                if (state.aiSummary.isNotBlank()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (state.aiEmotion.isNotBlank()) {
                                Text(
                                    text = "Emotion: ${state.aiEmotion}",
                                    fontSize = 13.sp,
                                    color = Color(0xFF8B6F47),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = state.aiSummary,
                                fontSize = 14.sp,
                                color = Color(0xFF3D3D3D)
                            )
                            state.aiGuidance.forEach {
                                Text(
                                    text = "- $it",
                                    fontSize = 13.sp,
                                    color = Color(0xFF6B6B6B)
                                )
                            }
                        }
                    }
                }

                // Save button
                Button(
                    onClick = {
                        if (contentText.isNotBlank()) {
                            val entryForSelectedDay = state.entries.firstOrNull {
                                isSameDayNew(it.createdAtEpochMillis, selectedDate)
                            }

                            if (isEditMode && entryToEdit != null) {
                                viewModel.updateEntry(
                                    id = entryToEdit.id,
                                    content = contentText,
                                    mood = moodText,
                                    dateMillis = selectedDate,
                                    onUpdated = onEntrySaved
                                )
                            } else if (entryForSelectedDay != null) {
                                viewModel.updateEntry(
                                    id = entryForSelectedDay.id,
                                    content = contentText,
                                    mood = moodText,
                                    dateMillis = selectedDate,
                                    onUpdated = onEntrySaved
                                )
                            } else {
                                viewModel.saveEntryWithDate(
                                    content = contentText,
                                    mood = moodText,
                                    dateMillis = selectedDate,
                                    onSaved = onEntrySaved
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B6F47)
                    ),
                    enabled = contentText.isNotBlank()
                ) {
                    Text(
                        text = if (isEditMode) "Save Changes" else "Save Entry",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = {
                        shareEntryBackup(
                            context = context,
                            dateMillis = selectedDate,
                            mood = moodText,
                            content = contentText
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    enabled = contentText.isNotBlank(),
                    border = BorderStroke(1.dp, Color(0xFF8B6F47))
                ) {
                    Text(
                        text = "Save to Google Keep",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF8B6F47)
                    )
                }

                Text(
                    text = "This opens Google Keep directly when it is installed. If not, your phone will show the normal share sheet.",
                    fontSize = 12.sp,
                    color = Color(0xFF7A6B57),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    Row(modifier = Modifier.fillMaxWidth()) {
        if (message.fromUser) {
            Spacer(modifier = Modifier.weight(1f))
        }
        Surface(
            color = if (message.fromUser) Color(0xFF8B6F47) else Color(0xFFF2E9DC),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                color = if (message.fromUser) Color.White else Color(0xFF3D3D3D),
                fontSize = 13.sp,
                textAlign = TextAlign.Start
            )
        }
        if (!message.fromUser) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun DateGridView(
    monthStartMillis: Long,
    selectedDate: Long,
    onDateSelected: (Long) -> Unit
) {
    val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
    
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    fontSize = 10.sp,
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
                        
                        val isSelected = isSameDayNew(selectedDate, dayDate.timeInMillis)
                        
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .padding(2.dp)
                                .clickable { onDateSelected(dayDate.timeInMillis) },
                            color = if (isSelected) Color(0xFF8B6F47) else Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = dayCounter.toString(),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF3D3D3D),
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

private fun formatDateOnly(epochMillis: Long): String {
    val format = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
    return format.format(Date(epochMillis))
}

private fun formatMonthYear(epochMillis: Long): String {
    val format = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    return format.format(Date(epochMillis))
}

private fun isSameDayNew(millis1: Long, millis2: Long): Boolean {
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

