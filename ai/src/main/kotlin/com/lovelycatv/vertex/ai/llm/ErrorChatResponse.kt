package com.lovelycatv.vertex.ai.llm

class ErrorChatResponse(
    val errorMessage: String,
    originalResponse: String
) : ChatResponse(false, "", 0L, "", emptyList(), Usage(), originalResponse)