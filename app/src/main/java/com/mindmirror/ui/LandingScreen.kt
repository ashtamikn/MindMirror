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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LandingScreen(
    onOpenBook: () -> Unit,
    onNewEntry: () -> Unit,
    onChangeLock: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFAF7F2) // Warm paper-like color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Decorative top element
            Text(
                text = "✦",
                fontSize = 32.sp,
                color = Color(0xFFD4A574) // Warm gold
            )
            
            Spacer(modifier = Modifier.height(40.dp))

            // Main title
            Text(
                text = "Dear Diary",
                fontSize = 64.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFF3D3D3D),
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text = "Your thoughts, your stories, your reflections",
                fontSize = 14.sp,
                color = Color(0xFF6B6B6B),
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Open Book Button
            BookStyleButton(
                icon = Icons.Default.Home,
                title = "Open Book",
                subtitle = "Browse past entries",
                onClick = onOpenBook,
                backgroundColor = Color(0xFF8B6F47),
                contentColor = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            // New Entry Button
            BookStyleButton(
                icon = Icons.Default.Add,
                title = "New Page",
                subtitle = "Write a new entry",
                onClick = onNewEntry,
                backgroundColor = Color(0xFFD4A574),
                contentColor = Color(0xFF3D3D3D)
            )

            Spacer(modifier = Modifier.height(20.dp))

            BookStyleButton(
                icon = Icons.Default.Lock,
                title = "Change Passphrase",
                subtitle = "Edit your personal question",
                onClick = onChangeLock,
                backgroundColor = Color(0xFFEEE2D3),
                contentColor = Color(0xFF5A4630)
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Decorative bottom element
            Text(
                text = "✦",
                fontSize = 32.sp,
                color = Color(0xFFD4A574)
            )
        }
    }
}

@Composable
private fun BookStyleButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    contentColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(40.dp),
                tint = contentColor
            )

            Spacer(modifier = Modifier.padding(start = 20.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

