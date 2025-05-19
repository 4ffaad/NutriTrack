package com.daffa_34076492.nutritrack.model

data class GeminiRequest(
    val contents: List<RequestContent>,
    val generationConfig: GenerationConfig? = null // add this line
)

data class RequestContent(
    val parts: List<RequestPart>
)

data class RequestPart(
    val text: String
)

data class GenerationConfig(
    val temperature: Float = 0.7f,
    val maxOutputTokens: Int = 20,
    val topP: Float = 1.0f,
    val topK: Int = 1
)