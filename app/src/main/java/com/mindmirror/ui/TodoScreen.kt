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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindmirror.data.TodoEntity
import com.mindmirror.data.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun TodoScreen(
    todoRepository: TodoRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val todos by todoRepository.getAllTodos().collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTodo by remember { mutableStateOf<TodoEntity?>(null) }
    var newTodoText by rememberSaveable { mutableStateOf("") }

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
                        text = "My Tasks",
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
                if (todos.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "No tasks",
                            tint = DiaryTheme.accentGreen,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Tasks Yet",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DiaryTheme.darkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap + to add your first task",
                            fontSize = 14.sp,
                            color = DiaryTheme.subtleText
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(todos, key = { it.id }) { todo ->
                            TodoItem(
                                todo = todo,
                                onToggle = {
                                    scope.launch {
                                        todoRepository.toggleTodoComplete(todo)
                                    }
                                },
                                onEdit = {
                                    editingTodo = todo
                                    newTodoText = todo.text
                                },
                                onDelete = {
                                    scope.launch {
                                        todoRepository.deleteTodo(todo)
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
                        newTodoText = ""
                        showAddDialog = true 
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = DiaryTheme.accentGreen,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Todo")
                }
            }
        }
    }

    // Add/Edit Dialog
    if (showAddDialog || editingTodo != null) {
        Dialog(
            onDismissRequest = {
                showAddDialog = false
                editingTodo = null
                newTodoText = ""
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
                        text = if (editingTodo != null) "Edit Task" else "New Task",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = DiaryTheme.darkText
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = newTodoText,
                        onValueChange = { value -> newTodoText = value },
                        label = { Text("Task") },
                        placeholder = { Text("What do you need to do?") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DiaryTheme.accentGreen,
                            unfocusedBorderColor = DiaryTheme.subtleText,
                            focusedTextColor = DiaryTheme.darkText,
                            unfocusedTextColor = DiaryTheme.darkText,
                            focusedLabelColor = DiaryTheme.accentGreen,
                            unfocusedLabelColor = DiaryTheme.subtleText,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                showAddDialog = false
                                editingTodo = null
                                newTodoText = ""
                            }
                        ) {
                            Text("Cancel", color = DiaryTheme.subtleText)
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        TextButton(
                            onClick = {
                                val textToSave = newTodoText.trim()
                                if (textToSave.isNotEmpty()) {
                                    scope.launch {
                                        if (editingTodo != null) {
                                            todoRepository.updateTodo(editingTodo!!.copy(text = textToSave))
                                        } else {
                                            todoRepository.addTodo(textToSave)
                                        }
                                    }
                                }
                                showAddDialog = false
                                editingTodo = null
                                newTodoText = ""
                            }
                        ) {
                            Text(
                                "Save",
                                color = DiaryTheme.accentGreen,
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
fun TodoTabContent(
    todoRepository: TodoRepository,
    scope: CoroutineScope
) {
    val todos by todoRepository.getAllTodos().collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTodo by remember { mutableStateOf<TodoEntity?>(null) }
    var newTodoText by rememberSaveable { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (todos.isEmpty()) {
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
                            imageVector = Icons.Default.Check,
                            contentDescription = "Todo",
                            tint = DiaryTheme.accentGreen,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Tasks Yet",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DiaryTheme.darkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap + to add your first task",
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
                    items(todos, key = { it.id }) { todo ->
                        TodoItem(
                            todo = todo,
                            onToggle = {
                                scope.launch {
                                    todoRepository.toggleTodoComplete(todo)
                                }
                            },
                            onEdit = {
                                editingTodo = todo
                                newTodoText = todo.text
                            },
                            onDelete = {
                                scope.launch {
                                    todoRepository.deleteTodo(todo)
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

        // FAB to add new todo
        FloatingActionButton(
            onClick = { 
                newTodoText = ""
                showAddDialog = true 
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = DiaryTheme.accentGreen,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Todo")
        }
    }

    // Add/Edit Dialog
    if (showAddDialog || editingTodo != null) {
        Dialog(
            onDismissRequest = {
                showAddDialog = false
                editingTodo = null
                newTodoText = ""
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
                        text = if (editingTodo != null) "Edit Task" else "New Task",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = DiaryTheme.primaryBrown
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = newTodoText,
                        onValueChange = { value -> newTodoText = value },
                        label = { Text("Task") },
                        placeholder = { Text("What do you need to do?") },
                        modifier = Modifier.fillMaxWidth(),
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
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                showAddDialog = false
                                editingTodo = null
                                newTodoText = ""
                            }
                        ) {
                            Text("Cancel", color = DiaryTheme.subtleText)
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        TextButton(
                            onClick = {
                                val textToSave = newTodoText.trim()
                                scope.launch {
                                    if (editingTodo != null) {
                                        todoRepository.updateTodo(editingTodo!!.copy(text = textToSave))
                                    } else {
                                        if (textToSave.isNotEmpty()) {
                                            todoRepository.addTodo(textToSave)
                                        }
                                    }
                                }
                                showAddDialog = false
                                editingTodo = null
                                newTodoText = ""
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
private fun TodoItem(
    todo: TodoEntity,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (todo.isCompleted) DiaryTheme.lightCard.copy(alpha = 0.6f) else Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = if (todo.isCompleted) 0.dp else 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = DiaryTheme.accentGreen,
                    uncheckedColor = DiaryTheme.subtleText
                )
            )
            
            Text(
                text = todo.text,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                fontSize = 16.sp,
                color = if (todo.isCompleted) DiaryTheme.subtleText else DiaryTheme.darkText,
                textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 3
            )
            
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = DiaryTheme.accentGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFE57373),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
