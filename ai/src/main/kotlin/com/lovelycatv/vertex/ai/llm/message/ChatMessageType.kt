package com.lovelycatv.vertex.ai.llm.message

enum class ChatMessageType(val typeId: Int) {
    USER(0),
    ASSISTANT(1),
    SYSTEM(2),
    TOOL(3);

    companion object {
        fun getTypeById(typeId: Int): ChatMessageType {
            return entries.find { it.typeId == typeId }
                ?: throw IllegalArgumentException("Unknown message type id: $typeId")
        }
    }
}