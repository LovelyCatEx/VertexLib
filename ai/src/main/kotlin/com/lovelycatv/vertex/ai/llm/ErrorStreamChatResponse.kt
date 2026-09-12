package com.lovelycatv.vertex.ai.llm

class ErrorStreamChatResponse(
    val errorMessage: String,
    originalResponse: String
) : StreamChatResponse("", 0L, "", emptyList(), Usage(), false, originalResponse)