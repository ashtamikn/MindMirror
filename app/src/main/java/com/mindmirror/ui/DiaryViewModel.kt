package com.mindmirror.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmirror.ai.CompanionAi
import com.mindmirror.data.DiaryEntry
import com.mindmirror.data.DiaryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: Long,
    val text: String,
    val fromUser: Boolean
)

private fun initialChatMessages(): List<ChatMessage> {
    return listOf(
        ChatMessage(
            id = System.currentTimeMillis(),
            text = "Hey, I am here with you. Start writing and I will respond like a friend.",
            fromUser = false
        )
    )
}

data class DiaryUiState(
    val contentDraft: String = "",
    val moodDraft: String = "",
    val entries: List<DiaryEntry> = emptyList(),
    val insights: Insights = InsightsAnalyzer.analyze(emptyList()),
    val aiEmotion: String = "",
    val aiSummary: String = "",
    val aiGuidance: List<String> = emptyList(),
    val aiLoading: Boolean = false,
    val aiError: String? = null,
    val chatMessages: List<ChatMessage> = initialChatMessages(),
    val isChatTyping: Boolean = false
)

class DiaryViewModel(
    private val repository: DiaryRepository,
    private val companionAi: CompanionAi? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(DiaryUiState())
    val uiState: StateFlow<DiaryUiState> = _uiState.asStateFlow()
    private var chatReplyJob: Job? = null
    private var lastChatFingerprint: String = ""

    init {
        viewModelScope.launch {
            repository.observeEntries().collect { entries ->
                _uiState.update {
                    it.copy(entries = entries, insights = InsightsAnalyzer.analyze(entries))
                }
            }
        }
    }

    fun updateContentDraft(value: String) {
        _uiState.update { it.copy(contentDraft = value) }
    }

    fun updateMoodDraft(value: String) {
        _uiState.update { it.copy(moodDraft = value) }
    }

    fun saveEntry() {
        val state = _uiState.value
        if (state.contentDraft.isBlank()) return

        viewModelScope.launch {
            repository.addEntry(content = state.contentDraft, mood = state.moodDraft)
            _uiState.update { it.copy(contentDraft = "", moodDraft = "") }
        }
    }

    fun saveEntryWithDate(
        content: String,
        mood: String,
        dateMillis: Long,
        onSaved: (Long) -> Unit = {}
    ) {
        if (content.isBlank()) return

        viewModelScope.launch {
            val id = repository.addEntryWithDate(content = content, mood = mood, dateMillis = dateMillis)
            clearReflection()
            onSaved(id)
        }
    }

    fun updateEntry(
        id: Long,
        content: String,
        mood: String,
        dateMillis: Long,
        onUpdated: (Long) -> Unit = {}
    ) {
        if (content.isBlank()) return

        viewModelScope.launch {
            repository.updateEntry(id = id, content = content, mood = mood, dateMillis = dateMillis)
            clearReflection()
            onUpdated(id)
        }
    }

    fun generateReflectionForText(content: String, mood: String) {
        _uiState.update { it.copy(aiLoading = true, aiError = null) }

        viewModelScope.launch {
            runCatching {
                companionAi?.generateReflection(
                    content = content,
                    mood = mood,
                    recentEntries = _uiState.value.entries
                ) ?: InsightsAnalyzer.reflect(
                    content = content,
                    mood = mood,
                    recentEntries = _uiState.value.entries
                )
            }.onSuccess { advice ->
                _uiState.update {
                    it.copy(
                        aiEmotion = advice.emotion,
                        aiSummary = advice.summary,
                        aiGuidance = advice.guidance,
                        aiLoading = false,
                        aiError = null
                    )
                }
            }.onFailure {
                val fallback = InsightsAnalyzer.reflect(
                    content = content,
                    mood = mood,
                    recentEntries = _uiState.value.entries
                )
                _uiState.update {
                    it.copy(
                        aiEmotion = fallback.emotion,
                        aiSummary = fallback.summary,
                        aiGuidance = fallback.guidance,
                        aiLoading = false,
                        aiError = null
                    )
                }
            }
        }
    }

    fun generateReflectionForEntry(entry: DiaryEntry) {
        generateReflectionForText(content = entry.content, mood = entry.mood)
    }

    fun clearReflection() {
        _uiState.update {
            it.copy(
                aiSummary = "",
                aiEmotion = "",
                aiGuidance = emptyList(),
                aiLoading = false,
                aiError = null
            )
        }
    }

    fun resetCompanionChat() {
        chatReplyJob?.cancel()
        lastChatFingerprint = ""
        _uiState.update {
            it.copy(
                chatMessages = initialChatMessages(),
                isChatTyping = false
            )
        }
    }

    fun onWritingDraftChanged(content: String, mood: String) {
        val trimmedContent = content.trim()
        val fingerprint = "${trimmedContent.lowercase()}|${mood.trim().lowercase()}"
        if (trimmedContent.isBlank() || trimmedContent.length < 8 || fingerprint == lastChatFingerprint) {
            chatReplyJob?.cancel()
            _uiState.update { it.copy(isChatTyping = false) }
            return
        }

        chatReplyJob?.cancel()
        _uiState.update { it.copy(isChatTyping = true) }
        chatReplyJob = viewModelScope.launch {
            delay(650)

            val reply = buildFriendlyReply(content = content, mood = mood)
            coroutineContext.ensureActive()
            lastChatFingerprint = fingerprint
            _uiState.update { state ->
                val updated = (state.chatMessages + ChatMessage(
                    id = System.currentTimeMillis(),
                    text = summarizeUserThoughtForBubble(content),
                    fromUser = true
                ) + ChatMessage(
                    id = System.currentTimeMillis() + 1,
                    text = reply,
                    fromUser = false
                )).takeLast(8)
                state.copy(chatMessages = updated, isChatTyping = false)
            }
        }
    }

    private suspend fun buildFriendlyReply(content: String, mood: String): String {
        val remote = runCatching {
            companionAi?.generateChatReply(content = content, mood = mood, recentEntries = _uiState.value.entries)
        }.getOrNull()?.takeIf { it.isNotBlank() }
        if (remote != null) return remote

        val advice = InsightsAnalyzer.reflect(content = content, mood = mood, recentEntries = _uiState.value.entries)
        val firstTip = advice.guidance.firstOrNull()
        val intros = listOf(
            "I hear you.",
            "That sounds heavy.",
            "Thanks for sharing this honestly.",
            "You are doing the right thing by writing it out."
        )
        val intro = intros[(content.length + mood.length).mod(intros.size)]
        return if (firstTip != null) {
            "$intro ${advice.summary} Try this next: $firstTip"
        } else {
            "$intro ${advice.summary}"
        }
    }

    private fun summarizeUserThoughtForBubble(content: String): String {
        val clean = content.replace("\\s+".toRegex(), " ").trim()
        val sentence = clean.split(Regex("(?<=[.!?])\\s+")).lastOrNull()?.trim().orEmpty()
        return sentence.takeIf { it.isNotBlank() }?.take(120) ?: clean.take(120)
    }
}

