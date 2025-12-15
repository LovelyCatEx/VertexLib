package com.lovelycatv.vertex.ai.openai

import com.google.gson.annotations.SerializedName

/**
 * @author lovelycat
 * @since 2025-12-13 02:00
 * @version 1.0
 */
enum class ChatMessageRole(val roleName: String) {
    @SerializedName("system")
    SYSTEM("system"),
    @SerializedName("user")
    USER("user"),
    @SerializedName("assistant")
    ASSISTANT("assistant"),
    @SerializedName("tool")
    TOOL("tool");
}