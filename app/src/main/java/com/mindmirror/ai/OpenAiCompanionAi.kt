package com.mindmirror.ai

import com.mindmirror.data.DiaryEntry
import com.mindmirror.ui.ReflectionAdvice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class OpenAiCompanionAi(
    private val apiKey: String,
    private val baseUrl: String,
    private val model: String,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .build()
) : CompanionAi {

    override suspend fun generateChatReply(
        content: String,
        mood: String,
        recentEntries: List<DiaryEntry>
    ): String? = withContext(Dispatchers.IO) {
        val prompt = buildString {
            append("User mood: ")
            append(mood.ifBlank { "unspecified" })
            append("\nRecent entries count: ")
            append(recentEntries.size)
            append("\nLatest reflection:\n")
            append(content)
        }

        val system = "You are a warm, emotionally intelligent diary friend. Reply like a real friend, vary tone, avoid repetitive openings, and keep it to 2-4 short sentences with one practical next step."
        requestText(systemPrompt = system, userPrompt = prompt, temperature = 0.85)
    }

    override suspend fun generateReflection(
        content: String,
        mood: String,
        recentEntries: List<DiaryEntry>
    ): ReflectionAdvice? = withContext(Dispatchers.IO) {
        val prompt = buildString {
            append("Mood: ")
            append(mood.ifBlank { "unspecified" })
            append("\nRecent entries count: ")
            append(recentEntries.size)
            append("\nEntry:\n")
            append(content)
            append("\n\nReturn exactly this format (no extra text):\n")
            append("EMOTION: <one word or short phrase>\n")
            append("SUMMARY: <one concise summary sentence grounded in the entry>\n")
            append("ADVICE:\n")
            append("- <actionable step 1>\n")
            append("- <actionable step 2>\n")
            append("- <optional step 3>")
        }

        val system = "Analyze emotion from diary text. Do not repeat input. Give Emotion, Advice, Summary. Keep output grounded in user text and avoid generic cliches."
        val raw = requestText(systemPrompt = system, userPrompt = prompt, temperature = 0.6) ?: return@withContext null
        parseReflection(raw)
    }

    private fun parseReflection(raw: String): ReflectionAdvice {
        val jsonParsed = parseReflectionJson(raw)
        if (jsonParsed != null) return jsonParsed

        val lines = raw.lines().map { it.trim() }.filter { it.isNotBlank() }
        val emotionLine = lines.firstOrNull { it.startsWith("EMOTION:", ignoreCase = true) }
        val summaryLine = lines.firstOrNull { it.startsWith("SUMMARY:", ignoreCase = true) }
        val adviceStartIndex = lines.indexOfFirst { it.startsWith("ADVICE:", ignoreCase = true) }

        val emotion = emotionLine?.substringAfter(":")?.trim().orEmpty()
        val summary = summaryLine?.substringAfter(":")?.trim().orEmpty()
        val guidanceCandidates = if (adviceStartIndex >= 0) lines.drop(adviceStartIndex + 1) else lines
        val guidance = guidanceCandidates
            .filter { it.startsWith("-") || it.matches(Regex("^\\d+[.)].*")) }
            .map { it.removePrefix("-").trim() }
            .map { it.replace(Regex("^\\d+[.)]\\s*"), "").trim() }
            .filter { it.isNotBlank() }
            .take(4)

        return ReflectionAdvice(
            emotion = emotion.ifBlank { "Reflective" },
            summary = if (summary.isBlank()) raw.trim() else summary,
            guidance = if (guidance.isEmpty()) listOf("Take one small next step today and review how it feels.") else guidance
        )
    }

    private fun parseReflectionJson(raw: String): ReflectionAdvice? {
        return runCatching {
            val json = JSONObject(raw)
            val emotion = json.optString("emotion").ifBlank { json.optString("Emotion") }.trim()
            val summary = json.optString("summary").ifBlank { json.optString("Summary") }.trim()
            val adviceArray = json.optJSONArray("advice") ?: json.optJSONArray("guidance")
            val guidance = buildList {
                if (adviceArray != null) {
                    for (i in 0 until adviceArray.length()) {
                        val item = adviceArray.optString(i).trim()
                        if (item.isNotBlank()) add(item)
                    }
                }
            }

            ReflectionAdvice(
                emotion = emotion.ifBlank { "Reflective" },
                summary = summary.ifBlank { raw.trim() },
                guidance = guidance.ifEmpty { listOf("Take one small next step today and review how it feels.") }
            )
        }.getOrNull()
    }

    private fun requestText(systemPrompt: String, userPrompt: String, temperature: Double): String? {
        val messages = JSONArray()
            .put(JSONObject().put("role", "system").put("content", systemPrompt))
            .put(JSONObject().put("role", "user").put("content", userPrompt))

        val bodyJson = JSONObject()
            .put("model", model)
            .put("messages", messages)
            .put("temperature", temperature)
            .put("max_tokens", 220)

        val request = Request.Builder()
            .url(baseUrl.trimEnd('/') + "/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(bodyJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val body = response.body?.string() ?: return null
            val json = JSONObject(body)
            val choices = json.optJSONArray("choices") ?: return null
            if (choices.length() == 0) return null
            val content = choices.optJSONObject(0)
                ?.optJSONObject("message")
                ?.optString("content")
                ?.trim()
            return content?.takeIf { it.isNotBlank() }
        }
    }
}

