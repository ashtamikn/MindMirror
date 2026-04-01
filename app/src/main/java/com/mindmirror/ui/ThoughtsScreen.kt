package com.mindmirror.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.foundation.background
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindmirror.data.ThoughtEntity
import com.mindmirror.data.ThoughtRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ThoughtsScreen(
    thoughtRepository: ThoughtRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val thoughts by thoughtRepository.getAllThoughts().collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var editingThought by remember { mutableStateOf<ThoughtEntity?>(null) }
    var newThoughtText by rememberSaveable { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DiaryTheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DiaryTheme.primaryBrown)
                    .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Quick Thoughts",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            // Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (thoughts.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "No thoughts",
                            tint = DiaryTheme.accentGold,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Thoughts Yet",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DiaryTheme.darkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap + to capture your first thought",
                            fontSize = 14.sp,
                            color = DiaryTheme.subtleText
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(thoughts, key = { it.id }) { thought ->
                            ThoughtItem(
                                thought = thought,
                                onEdit = {
                                    editingThought = thought
                                    newThoughtText = thought.text
                                },
                                onDelete = {
                                    scope.launch {
                                        thoughtRepository.deleteThought(thought)
                                    }
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }

                // FAB
                FloatingActionButton(
                    onClick = { 
                        newThoughtText = ""
                        showAddDialog = true 
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = DiaryTheme.accentGold,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Thought")
                }
            }
        }
    }

    // Add/Edit Dialog
    if (showAddDialog || editingThought != null) {
        Dialog(
            onDismissRequest = {
                showAddDialog = false
                editingThought = null
                newThoughtText = ""
            }
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = DiaryTheme.background
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = if (editingThought != null) "Edit Thought" else "New Thought",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = DiaryTheme.primaryBrown
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = newThoughtText,
                        onValueChange = { value -> newThoughtText = value },
                        label = { Text("Thought") },
                        placeholder = { Text("What's on your mind?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DiaryTheme.primaryBrown,
                            unfocusedBorderColor = DiaryTheme.accentGold,
                            focusedTextColor = DiaryTheme.darkText,
                            unfocusedTextColor = DiaryTheme.darkText,
                            focusedLabelColor = DiaryTheme.primaryBrown,
                            unfocusedLabelColor = DiaryTheme.subtleText,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 5
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                showAddDialog = false
                                editingThought = null
                                newThoughtText = ""
                            }
                        ) {
                            Text("Cancel", color = DiaryTheme.subtleText)
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        TextButton(
                            onClick = {
                                val textToSave = newThoughtText.trim()
                                if (textToSave.isNotEmpty()) {
                                    scope.launch {
                                        if (editingThought != null) {
                                            thoughtRepository.updateThought(editingThought!!.copy(text = textToSave))
                                        } else {
                                            thoughtRepository.addThought(textToSave)
                                        }
                                    }
                                }
                                showAddDialog = false
                                editingThought = null
                                newThoughtText = ""
                            }
                        ) {
                            Text(
                                "Save",
                                color = DiaryTheme.primaryBrown,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThoughtsTabContent(
    thoughtRepository: ThoughtRepository,
    scope: CoroutineScope
) {
    val thoughts by thoughtRepository.getAllThoughts().collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var editingThought by remember { mutableStateOf<ThoughtEntity?>(null) }
    var newThoughtText by rememberSaveable { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (thoughts.isEmpty()) {
                Spacer(modifier = Modifier.height(40.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Thoughts",
                            tint = DiaryTheme.accentGold,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Thoughts Yet",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DiaryTheme.darkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap + to capture your first thought",
                            fontSize = 14.sp,
                            color = DiaryTheme.subtleText
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(thoughts, key = { it.id }) { thought ->
                        ThoughtItem(
                            thought = thought,
                            onEdit = {
                                editingThought = thought
                                newThoughtText = thought.text
                            },
                            onDelete = {
                                scope.launch {
                                    thoughtRepository.deleteThought(thought)
                                }
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // FAB to add new thought
        FloatingActionButton(
            onClick = { 
                newThoughtText = ""
                showAddDialog = true 
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = DiaryTheme.accentGold,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Thought")
        }
    }

    // Add/Edit Dialog
    if (showAddDialog || editingThought != null) {
        Dialog(
            onDismissRequest = {
                showAddDialog = false
                editingThought = null
                newThoughtText = ""
            }
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = DiaryTheme.background
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = if (editingThought != null) "Edit Thought" else "New Thought",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = DiaryTheme.primaryBrown
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = newThoughtText,
                        onValueChange = { value -> newThoughtText = value },
                        label = { Text("Thought") },
                        placeholder = { Text("What's on your mind?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DiaryTheme.primaryBrown,
                            unfocusedBorderColor = DiaryTheme.accentGold,
                            focusedTextColor = DiaryTheme.darkText,
                            unfocusedTextColor = DiaryTheme.darkText,
                            focusedLabelColor = DiaryTheme.primaryBrown,
                            unfocusedLabelColor = DiaryTheme.subtleText,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 5
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                showAddDialog = false
                                editingThought = null
                                newThoughtText = ""
                            }
                        ) {
                            Text("Cancel", color = DiaryTheme.subtleText)
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        TextButton(
                            onClick = {
                                val textToSave = newThoughtText.trim()
                                scope.launch {
                                    if (editingThought != null) {
                                        thoughtRepository.updateThought(editingThought!!.copy(text = textToSave))
                                    } else {
                                        if (textToSave.isNotEmpty()) {
                                            thoughtRepository.addThought(textToSave)
                                        }
                                    }
                                }
                                showAddDialog = false
                                editingThought = null
                                newThoughtText = ""
                            }
                        ) {
                            Text(
                                "Save",
                                color = DiaryTheme.primaryBrown,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThoughtItem(
    thought: ThoughtEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()) }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Thought",
                    tint = DiaryTheme.accentGold,
                    modifier = Modifier.size(20.dp)
                )
                
                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = DiaryTheme.primaryBrown,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = DiaryTheme.accentGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = thought.text,
                fontSize = 15.sp,
                color = DiaryTheme.darkText,
                lineHeight = 22.sp,
                maxLines = 10
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = dateFormat.format(Date(thought.createdAt)),
                fontSize = 12.sp,
                color = DiaryTheme.subtleText,
                fontStyle = FontStyle.Italic
            )
        }
    }
}
