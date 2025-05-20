package com.daffa_34076492.nutritrack.model

data class GeminiRequest(
    val contents: List<RequestContent>,
)

data class RequestContent(
    val parts: List<RequestPart>
)

data class RequestPart(
    val text: String
)


