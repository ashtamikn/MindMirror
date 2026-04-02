package com.mindmirror.data

import android.content.Context
import android.content.SharedPreferences

class DraftManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("diary_drafts", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_CONTENT_DRAFT = "content_draft"
        private const val KEY_MOOD_DRAFT = "mood_draft"
        private const val KEY_DATE_DRAFT = "date_draft"
    }
    
    fun saveContentDraft(content: String) {
        prefs.edit().putString(KEY_CONTENT_DRAFT, content).apply()
    }
    
    fun saveMoodDraft(mood: String) {
        prefs.edit().putString(KEY_MOOD_DRAFT, mood).apply()
    }
    
    fun saveDateDraft(dateMillis: Long) {
        prefs.edit().putLong(KEY_DATE_DRAFT, dateMillis).apply()
    }
    
    fun getContentDraft(): String {
        return prefs.getString(KEY_CONTENT_DRAFT, "") ?: ""
    }
    
    fun getMoodDraft(): String {
        return prefs.getString(KEY_MOOD_DRAFT, "") ?: ""
    }
    
    fun getDateDraft(): Long {
        return prefs.getLong(KEY_DATE_DRAFT, System.currentTimeMillis())
    }
    
    fun clearDrafts() {
        prefs.edit()
            .remove(KEY_CONTENT_DRAFT)
            .remove(KEY_MOOD_DRAFT)
            .remove(KEY_DATE_DRAFT)
            .apply()
    }
    
    fun hasDraft(): Boolean {
        return getContentDraft().isNotBlank()
    }
}
