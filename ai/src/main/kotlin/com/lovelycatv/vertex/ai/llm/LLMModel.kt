package com.lovelycatv.vertex.ai.llm

/**
 * A model offered by an endpoint, as returned by [LLMClient.listModels].
 *
 * The providers describe their catalogue differently — the OpenAI-compatible shape reports an
 * owning organization and little else, the Messages API reports a display name and the token
 * limits — so the fields only one of them supplies stay null on the other.
 */
data class LLMModel(
    val id: String,
    /** Human-readable name. Messages API only; callers fall back to [id]. */
    val displayName: String? = null,
    /** Release time in epoch seconds. */
    val createdAt: Long? = null,
    /** Owning organization. OpenAI-compatible endpoints only. */
    val ownedBy: String? = null,
    /** Context window in tokens. Messages API only. */
    val maxInputTokens: Int? = null,
    /** Ceiling for the request's `max_tokens`. Messages API only. */
    val maxOutputTokens: Int? = null,
)
