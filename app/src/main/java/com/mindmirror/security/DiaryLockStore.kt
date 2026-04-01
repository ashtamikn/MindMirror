package com.mindmirror.security

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

private val Context.diaryLockDataStore by preferencesDataStore(name = "diary_lock")

class DiaryLockStore(private val context: Context) {
    val state: Flow<DiaryLockState> = context.diaryLockDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            DiaryLockState(
                question = preferences[QUESTION_KEY].orEmpty(),
                salt = preferences[SALT_KEY].orEmpty(),
                passphraseHash = preferences[HASH_KEY].orEmpty()
            )
        }

    suspend fun saveCredentials(question: String, passphrase: String) {
        val normalizedQuestion = question.trim()
        val normalizedPassphrase = passphrase.trim()
        val saltBytes = ByteArray(SALT_BYTES_LENGTH).also(secureRandom::nextBytes)
        val salt = Base64.getEncoder().encodeToString(saltBytes)
        val hash = hashPassphrase(normalizedPassphrase, saltBytes)

        context.diaryLockDataStore.edit { preferences ->
            preferences[QUESTION_KEY] = normalizedQuestion
            preferences[SALT_KEY] = salt
            preferences[HASH_KEY] = hash
        }
    }

    suspend fun verifyPassphrase(passphrase: String): Boolean {
        val current = loadCurrentState() ?: return false
        return verifyPassphrase(
            passphrase = passphrase,
            saltBase64 = current.salt,
            expectedHash = current.passphraseHash
        )
    }

    private suspend fun loadCurrentState(): DiaryLockState? {
        return state.map { current -> current.takeIf { it.isConfigured } }.firstOrNull()
    }

    companion object {
        private val QUESTION_KEY = stringPreferencesKey("question")
        private val SALT_KEY = stringPreferencesKey("salt")
        private val HASH_KEY = stringPreferencesKey("hash")
        private const val SALT_BYTES_LENGTH = 16
        private const val PBKDF2_ITERATIONS = 120_000
        private const val DERIVED_KEY_LENGTH = 256
        private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
        private val secureRandom = SecureRandom()

        fun verifyPassphrase(
            passphrase: String,
            saltBase64: String,
            expectedHash: String
        ): Boolean {
            if (saltBase64.isBlank() || expectedHash.isBlank()) return false
            val saltBytes = runCatching { Base64.getDecoder().decode(saltBase64) }.getOrNull() ?: return false
            val computedHash = hashPassphrase(passphrase.trim(), saltBytes)
            return MessageDigest.isEqual(computedHash.toByteArray(), expectedHash.toByteArray())
        }

        private fun hashPassphrase(passphrase: String, saltBytes: ByteArray): String {
            val spec = PBEKeySpec(passphrase.toCharArray(), saltBytes, PBKDF2_ITERATIONS, DERIVED_KEY_LENGTH)
            val factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
            return Base64.getEncoder().encodeToString(factory.generateSecret(spec).encoded)
        }
    }
}

data class DiaryLockState(
    val question: String = "",
    val salt: String = "",
    val passphraseHash: String = ""
) {
    val isConfigured: Boolean
        get() = question.isNotBlank() && salt.isNotBlank() && passphraseHash.isNotBlank()
}

