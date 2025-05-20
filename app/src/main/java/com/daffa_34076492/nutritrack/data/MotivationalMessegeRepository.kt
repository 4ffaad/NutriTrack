package com.daffa_34076492.nutritrack.data

import android.content.Context
import android.util.Log
import com.daffa_34076492.nutritrack.api.GeminiApi
import com.daffa_34076492.nutritrack.data.local.AppDatabase
import com.daffa_34076492.nutritrack.model.GeminiRequest
import com.daffa_34076492.nutritrack.model.MotivationalMessage
import com.daffa_34076492.nutritrack.model.RequestContent
import com.daffa_34076492.nutritrack.model.RequestPart
import java.io.IOException


class MotivationalMessageRepository(context: Context) {
    private val motivationalDao = AppDatabase.getDatabase(context).motivationalMessageDao()

    suspend fun saveMessage(message: MotivationalMessage) {
        motivationalDao.insertMessage(message)
    }

    suspend fun getMessagesForUser(userId: Int): List<MotivationalMessage> {
        return motivationalDao.getMessagesForUser(userId)
    }
    suspend fun clearMessagesForUser(userId: Int) {
        motivationalDao.clearMessagesForUser(userId)
    }
    suspend fun getMotivationalMessage(prompt: String): String {
        try {
            val request = GeminiRequest(
                contents = listOf(
                    RequestContent(
                        parts = listOf(RequestPart(text = prompt))
                    )
                ),

            )

            val response = GeminiApi.service.generateMotivationalMessage(
                apiKey = "AIzaSyCbXhw9tn4fScNLkECfPK9WhpCg5vE55Z4",
                request = request
            )

            return response.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text
                ?: "No message received."

        } catch (e: Exception) {
            Log.e("MotivationalRepo", "HTTP error: ${e.message}", e)
            throw IOException("Server error occurred. Please try again.")
        }
    }

    suspend fun getDataPatterns(prompt: String): List<String> {
        try {
            val request = GeminiRequest(
                contents = listOf(
                    RequestContent(
                        parts = listOf(RequestPart(text = prompt))
                    )
                )
            )

            val response = GeminiApi.service.generateMotivationalMessage(
                apiKey = "AIzaSyCbXhw9tn4fScNLkECfPK9WhpCg5vE55Z4",
                request = request
            )

            val fullText = response.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text ?: return listOf("No patterns found.")

            return fullText
                .split("\n")
                .filter { it.trim().matches(Regex("^\\d+\\..*")) }

        } catch (e: Exception) {
            Log.e("GeminiRepo", "Data pattern fetch error: ${e.message}", e)
            return listOf("Error occurred. Please try again.")
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: MotivationalMessageRepository? = null

        fun getInstance(context: Context): MotivationalMessageRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = MotivationalMessageRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}