package com.example.ai

import com.example.BuildConfig
import com.example.engine.TransliterationEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

sealed class AiResult {
    data class Success(val improvedText: String) : AiResult()
    data class Error(val message: String, val fallbackText: String) : AiResult()
}

object GeminiAiService {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    suspend fun improveNepaliText(inputText: String): AiResult = withContext(Dispatchers.IO) {
        if (inputText.isBlank()) {
            return@withContext AiResult.Error("Input is empty", "")
        }

        // Local transliteration as baseline fallback
        val localBaseline = TransliterationEngine.transliterateRomanNepali(inputText)

        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Local smart enhancement fallback
            val enhanced = if (!localBaseline.endsWith("।") && !localBaseline.endsWith("?")) {
                "$localBaseline।"
            } else {
                localBaseline
            }
            return@withContext AiResult.Success(enhanced)
        }

        try {
            val systemPrompt = "You are an expert native Nepali linguist. The user will provide Romanized Nepali or Devanagari text. Improve it into natural, grammatically correct, polite Nepali Devanagari script. Output ONLY the improved Nepali Devanagari text without quotes, Markdown, or English explanations."

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "System: $systemPrompt\n\nUser input: $inputText")
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.2)
                    put("topP", 0.95)
                })
            }

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: ""
                return@withContext AiResult.Error("API response ${response.code}: $errBody", localBaseline)
            }

            val responseString = response.body?.string() ?: ""
            val jsonResponse = JSONObject(responseString)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")?.trim()

            if (!text.isNullOrBlank()) {
                AiResult.Success(text.removeSurrounding("\"").removeSurrounding("'"))
            } else {
                AiResult.Success(localBaseline)
            }
        } catch (e: Exception) {
            AiResult.Error("Error: ${e.localizedMessage ?: "Unknown error"}", localBaseline)
        }
    }
}
