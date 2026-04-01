package com.mindmirror.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// App theme colors
object DiaryTheme {
    val background = Color(0xFFFAF7F2)
    val primaryBrown = Color(0xFF8B6F47)
    val accentGold = Color(0xFFD4A574)
    val accentGreen = Color(0xFF7CB587)
    val darkText = Color(0xFF3D3D3D)
    val subtleText = Color(0xFF6B6B6B)
    val cardBackground = Color(0xFF4A4A4A)
    val lightCard = Color(0xFFEEE2D3)
}

@Composable
fun LandingScreen(
    onOpenBook: () -> Unit,
    onNewEntry: () -> Unit,
    onChangeLock: () -> Unit,
    onOpenTodo: () -> Unit,
    onOpenThoughts: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DiaryTheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                DiaryTheme.primaryBrown,
                                DiaryTheme.primaryBrown.copy(alpha = 0.9f)
                            )
                        )
                    )
                    .padding(top = 48.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Dear Diary",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Light,
                                fontStyle = FontStyle.Italic,
                                color = Color.White
                            )
                            Text(
                                text = "Your personal space",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        
                        // Settings/Lock button
                        IconButton(
                            onClick = onChangeLock,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Change Lock",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
            
            // Modern Tab Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 20.dp),
                color = DiaryTheme.cardBackground.copy(alpha = 0.9f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TabItem(
                        icon = Icons.Outlined.Menu,
                        selectedIcon = Icons.Filled.Menu,
                        label = "Read",
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 }
                    )
                    TabItem(
                        icon = Icons.Outlined.Edit,
                        selectedIcon = Icons.Filled.Edit,
                        label = "Write",
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 }
                    )
                    TabItem(
                        icon = Icons.Outlined.CheckCircle,
                        selectedIcon = Icons.Filled.CheckCircle,
                        label = "Todo",
                        isSelected = selectedTab == 2,
                        onClick = { selectedTab = 2 }
                    )
                    TabItem(
                        icon = Icons.Outlined.Star,
                        selectedIcon = Icons.Filled.Star,
                        label = "Thoughts",
                        isSelected = selectedTab == 3,
                        onClick = { selectedTab = 3 }
                    )
                }
            }
            
            // Content area based on selected tab
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(24.dp)
            ) {
                when (selectedTab) {
                    0 -> ReadTabContent(onOpenBook = onOpenBook)
                    1 -> WriteTabContent(onNewEntry = onNewEntry)
                    2 -> TodoTabPlaceholder(onOpenTodo = onOpenTodo)
                    3 -> ThoughtsTabPlaceholder(onOpenThoughts = onOpenThoughts)
                }
            }
        }
    }
}

@Composable
private fun TabItem(
    icon: ImageVector,
    selectedIcon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) DiaryTheme.accentGreen else Color.Transparent,
        label = "tabBackground"
    )
    
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isSelected) selectedIcon else icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ReadTabContent(onOpenBook: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        
        // Feature card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenBook),
            color = Color.White,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Read",
                    tint = DiaryTheme.primaryBrown,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Open Your Book",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DiaryTheme.darkText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Browse through your past entries and memories",
                    fontSize = 14.sp,
                    color = DiaryTheme.subtleText,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Quick stats or recent entries placeholder
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DiaryTheme.lightCard,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Recent Entries",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DiaryTheme.darkText
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Tap above to view all your diary entries",
                    fontSize = 13.sp,
                    color = DiaryTheme.subtleText
                )
            }
        }
    }
}

@Composable
private fun WriteTabContent(onNewEntry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        
        // Write new entry card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNewEntry),
            color = Color.White,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(DiaryTheme.accentGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Entry",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "New Page",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DiaryTheme.darkText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Start writing your thoughts today",
                    fontSize = 14.sp,
                    color = DiaryTheme.subtleText,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Writing prompt
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DiaryTheme.accentGold.copy(alpha = 0.2f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "💡 Writing Prompt",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DiaryTheme.darkText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "What made you smile today?",
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = DiaryTheme.subtleText
                )
            }
        }
    }
}

@Composable
private fun TodoTabPlaceholder(onOpenTodo: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenTodo),
            color = Color.White,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(DiaryTheme.accentGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Todo",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "My Tasks",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DiaryTheme.darkText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap to manage your todo list",
                    fontSize = 14.sp,
                    color = DiaryTheme.subtleText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ThoughtsTabPlaceholder(onOpenThoughts: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenThoughts),
            color = Color.White,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(DiaryTheme.accentGold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Thoughts",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Quick Thoughts",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DiaryTheme.darkText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap to capture your ideas",
                    fontSize = 14.sp,
                    color = DiaryTheme.subtleText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


