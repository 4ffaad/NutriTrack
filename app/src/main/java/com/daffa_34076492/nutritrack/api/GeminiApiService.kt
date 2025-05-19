package com.daffa_34076492.nutritrack.api

import com.daffa_34076492.nutritrack.model.GeminiRequest
import com.daffa_34076492.nutritrack.model.GeminiResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApi {
    @Headers("Content-Type: application/json")
    @POST("v1/models/gemini-1.5-flash:generateContent")
    suspend fun generateMotivationalMessage(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse

    companion object {
        val service: GeminiApi by lazy {
            Retrofit.Builder()
                .baseUrl("https://generativelanguage.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GeminiApi::class.java)
        }
    }
}
