package com.lovelycatv.vertex.ai.response

import com.google.gson.annotations.SerializedName

/**
 * @author lovelycat
 * @since 2025-12-13 03:20
 * @version 1.0
 */
data class ListModelsResponse(
    @SerializedName("object")
    val obj: String,
    val data: List<Model>
) {
    data class Model(
        val id: String,
        @SerializedName("object")
        val obj: String,
        @SerializedName("owned_by")
        val ownedBy: String
    )
}