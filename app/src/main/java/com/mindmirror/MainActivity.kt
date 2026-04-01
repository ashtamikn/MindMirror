package com.mindmirror

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.mindmirror.ai.CompanionAi
import com.mindmirror.ai.OpenAiCompanionAi
import com.mindmirror.data.DiaryDatabase
import com.mindmirror.data.DiaryRepository
import com.mindmirror.ui.BookLibraryScreen
import com.mindmirror.ui.DiaryViewModel
import com.mindmirror.ui.EntryReaderScreen
import com.mindmirror.ui.LandingScreen
import com.mindmirror.ui.NewEntryScreen
import com.mindmirror.ui.theme.MindMirrorTheme
import java.util.Calendar

private const val ROUTE_LANDING = "landing"
private const val ROUTE_LIBRARY = "library"
private const val ROUTE_NEW_ENTRY = "newEntry"
private const val ROUTE_READER = "reader"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = Room.databaseBuilder(
            applicationContext,
            DiaryDatabase::class.java,
            "mindmirror.db"
        )
            .allowMainThreadQueries()
            .build()

        val repository = DiaryRepository(database.diaryDao())
        val remoteCompanionAi: CompanionAi? = if (
            BuildConfig.LLM_REMOTE_ENABLED && BuildConfig.LLM_API_KEY.isNotBlank()
        ) {
            OpenAiCompanionAi(
                apiKey = BuildConfig.LLM_API_KEY,
                baseUrl = BuildConfig.LLM_BASE_URL,
                model = BuildConfig.LLM_MODEL
            )
        } else {
            null
        }

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                if (modelClass.isAssignableFrom(DiaryViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return DiaryViewModel(
                        repository = repository,
                        companionAi = remoteCompanionAi
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }

        setContent {
            MindMirrorTheme {
                Surface {
                    val navController = rememberNavController()
                    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
                    var selectedEntryId by remember { mutableStateOf<Long?>(null) }
                    var isEditMode by remember { mutableStateOf(false) }

                    val viewModel: DiaryViewModel = viewModel(factory = factory)
                    val uiState by viewModel.uiState.collectAsState()

                    NavHost(navController = navController, startDestination = ROUTE_LANDING) {
                        composable(ROUTE_LANDING) {
                            LandingScreen(
                                onOpenBook = {
                                    navController.navigate(ROUTE_LIBRARY)
                                },
                                onNewEntry = {
                                    selectedDateMillis = null
                                    selectedEntryId = null
                                    isEditMode = false
                                    navController.navigate(ROUTE_NEW_ENTRY)
                                }
                            )
                        }

                        composable(ROUTE_LIBRARY) {
                            BookLibraryScreen(
                                viewModelFactory = factory,
                                onBackToHome = { navController.popBackStack() },
                                onSelectDate = { dateMillis ->
                                    val existingEntry = uiState.entries.firstOrNull {
                                        isSameDay(it.createdAtEpochMillis, dateMillis)
                                    }
                                    if (existingEntry != null) {
                                        selectedEntryId = existingEntry.id
                                        selectedDateMillis = existingEntry.createdAtEpochMillis
                                        isEditMode = false
                                        navController.navigate(ROUTE_READER)
                                    } else {
                                        selectedEntryId = null
                                        selectedDateMillis = dateMillis
                                        isEditMode = false
                                        navController.navigate(ROUTE_NEW_ENTRY)
                                    }
                                }
                            )
                        }

                        composable(ROUTE_NEW_ENTRY) {
                            val entryToEdit = if (isEditMode) {
                                uiState.entries.firstOrNull { it.id == selectedEntryId }
                            } else {
                                null
                            }
                            NewEntryScreen(
                                viewModelFactory = factory,
                                selectedDateMillis = selectedDateMillis,
                                entryToEdit = entryToEdit,
                                isEditMode = isEditMode,
                                onBack = { navController.popBackStack() },
                                onEntrySaved = { savedEntryId ->
                                    selectedEntryId = savedEntryId
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(ROUTE_READER) {
                            val entryToDisplay = uiState.entries.firstOrNull { it.id == selectedEntryId }
                                ?: uiState.entries.firstOrNull {
                                    selectedDateMillis != null && isSameDay(
                                        it.createdAtEpochMillis,
                                        selectedDateMillis!!
                                    )
                                }

                            if (entryToDisplay != null) {
                                EntryReaderScreen(
                                    entry = entryToDisplay,
                                    aiEmotion = uiState.aiEmotion,
                                    aiSummary = uiState.aiSummary,
                                    aiGuidance = uiState.aiGuidance,
                                    aiLoading = uiState.aiLoading,
                                    aiError = uiState.aiError,
                                    onBack = {
                                        viewModel.clearReflection()
                                        navController.popBackStack()
                                    },
                                    onEdit = {
                                        selectedEntryId = entryToDisplay.id
                                        selectedDateMillis = entryToDisplay.createdAtEpochMillis
                                        isEditMode = true
                                        navController.navigate(ROUTE_NEW_ENTRY)
                                    },
                                    onSummarize = {
                                        viewModel.generateReflectionForEntry(entryToDisplay)
                                    }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.popBackStack()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun isSameDay(millis1: Long, millis2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = millis1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = millis2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
        cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
        cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}

