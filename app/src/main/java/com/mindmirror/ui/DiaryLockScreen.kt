package com.mindmirror.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindmirror.R

enum class DiaryLockMode {
    Setup,
    Unlock,
    Change
}

@Composable
fun DiaryLockScreen(
    mode: DiaryLockMode,
    question: String,
    errorMessage: String?,
    onUnlock: (String) -> Unit,
    onCreateLock: (String, String) -> Unit,
    onChangeLock: (String, String, String) -> Unit,
    onCancelChange: (() -> Unit)? = null
) {
    var currentPassphrase by remember(mode) { mutableStateOf("") }
    var personalQuestion by remember(mode, question) { mutableStateOf(if (mode == DiaryLockMode.Setup) "" else question) }
    var newPassphrase by remember(mode) { mutableStateOf("") }
    var confirmPassphrase by remember(mode) { mutableStateOf("") }

    val isSetupMode = mode == DiaryLockMode.Setup
    val isUnlockMode = mode == DiaryLockMode.Unlock
    val isChangeMode = mode == DiaryLockMode.Change
    val passphrasesMatch = newPassphrase.trim() == confirmPassphrase.trim()

    // Handle system back button in Change mode
    if (isChangeMode && onCancelChange != null) {
        BackHandler {
            onCancelChange()
        }
    }

    if (isUnlockMode) {
        // Unlock mode: Full screen image background with passphrase at bottom
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.peeping),
                contentDescription = "Lock screen background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))
                
                // Dark rounded container positioned in the middle-lower area
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF4A4A4A).copy(alpha = 0.85f),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Show the user's personal question
                        Text(
                            text = question,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        
                        // Text field with lock icon
                        TextField(
                            value = newPassphrase,
                            onValueChange = { newPassphrase = it },
                            placeholder = { 
                                Text(
                                    "Answer to unlock...",
                                    color = Color(0xFF666666)
                                ) 
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Lock",
                                    tint = Color(0xFFD4A574),
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(28.dp)),
                            visualTransformation = PasswordVisualTransformation(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(28.dp),
                            singleLine = true
                        )
                        
                        if (!errorMessage.isNullOrBlank()) {
                            Text(
                                text = errorMessage,
                                color = Color(0xFFFF6B6B),
                                fontSize = 13.sp
                            )
                        }
                        
                        // Green unlock button
                        Button(
                            onClick = { onUnlock(newPassphrase) },
                            enabled = newPassphrase.trim().isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7CB587),
                                disabledContainerColor = Color(0xFF7CB587).copy(alpha = 0.5f)
                            )
                        ) {
                            Text(
                                text = "Unlock",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(0.3f))
            }
        }
    } else {
        // Setup and Change modes: Image background with dark themed form
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.peeping),
                contentDescription = "Lock screen background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 32.dp, vertical = 48.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dark rounded container matching unlock screen style
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF4A4A4A).copy(alpha = 0.85f),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Personal Question
                        Text(
                            text = "Personal Question",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        TextField(
                            value = personalQuestion,
                            onValueChange = { personalQuestion = it },
                            placeholder = { Text("Example: What do I call my favorite place?", color = Color(0xFF888888)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(28.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(28.dp),
                            singleLine = true
                        )

                        if (isChangeMode) {
                            Text(
                                text = "Current Passphrase",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            TextField(
                                value = currentPassphrase,
                                onValueChange = { currentPassphrase = it },
                                placeholder = { Text("Enter current passphrase", color = Color(0xFF888888)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Lock",
                                        tint = Color(0xFFD4A574),
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(28.dp)),
                                visualTransformation = PasswordVisualTransformation(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(28.dp),
                                singleLine = true
                            )
                        }

                        // New Passphrase
                        Text(
                            text = "New Passphrase",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        TextField(
                            value = newPassphrase,
                            onValueChange = { newPassphrase = it },
                            placeholder = { Text("Create a passphrase answer", color = Color(0xFF888888)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Lock",
                                    tint = Color(0xFFD4A574),
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(28.dp)),
                            visualTransformation = PasswordVisualTransformation(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(28.dp),
                            singleLine = true
                        )

                        // Confirm Passphrase
                        Text(
                            text = "Confirm Passphrase",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        TextField(
                            value = confirmPassphrase,
                            onValueChange = { confirmPassphrase = it },
                            placeholder = { Text("Re-enter the same passphrase", color = Color(0xFF888888)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Lock",
                                    tint = Color(0xFFD4A574),
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(28.dp)),
                            visualTransformation = PasswordVisualTransformation(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(28.dp),
                            singleLine = true
                        )

                        if (!errorMessage.isNullOrBlank()) {
                            Text(
                                text = errorMessage,
                                color = Color(0xFFFF6B6B),
                                fontSize = 13.sp
                            )
                        }

                        if (confirmPassphrase.isNotBlank() && !passphrasesMatch) {
                            Text(
                                text = "Passphrases do not match.",
                                color = Color(0xFFFF6B6B),
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Green button matching unlock screen
                        Button(
                            onClick = {
                                when (mode) {
                                    DiaryLockMode.Setup -> onCreateLock(personalQuestion, newPassphrase)
                                    DiaryLockMode.Unlock -> onUnlock(newPassphrase)
                                    DiaryLockMode.Change -> onChangeLock(currentPassphrase, personalQuestion, newPassphrase)
                                }
                            },
                            enabled = when (mode) {
                                DiaryLockMode.Setup -> personalQuestion.trim().length >= 6 && newPassphrase.trim().length >= 3 && passphrasesMatch
                                DiaryLockMode.Unlock -> newPassphrase.trim().isNotEmpty()
                                DiaryLockMode.Change -> currentPassphrase.trim().isNotEmpty() && personalQuestion.trim().length >= 6 && newPassphrase.trim().length >= 3 && passphrasesMatch
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(48.dp)
                                .align(Alignment.CenterHorizontally),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7CB587),
                                disabledContainerColor = Color(0xFF7CB587).copy(alpha = 0.5f)
                            )
                        ) {
                            Text(
                                text = when (mode) {
                                    DiaryLockMode.Setup -> "Create My Lock"
                                    DiaryLockMode.Unlock -> "Open My Diary"
                                    DiaryLockMode.Change -> "Save Changes"
                                },
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }

                        if (isChangeMode && onCancelChange != null) {
                            Button(
                                onClick = onCancelChange,
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(48.dp)
                                    .align(Alignment.CenterHorizontally),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4A574))
                            ) {
                                Text(
                                    text = "Cancel",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun diaryLockTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF8B6F47),
    unfocusedBorderColor = Color(0xFFD6C6B5),
    focusedContainerColor = Color(0xFFFFFCF8),
    unfocusedContainerColor = Color(0xFFFFFCF8)
)

