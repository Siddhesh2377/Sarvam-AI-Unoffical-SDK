package com.sarvam_ai.sarvam_sdk.api.api_interfaces

import com.sarvam_ai.sarvam_sdk.models.Models
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ChatRequest(
    val model: String = Models.SARVAM_M.modelId,
    val stream: Boolean = true,
    val messages: List<Message>
)

@JsonClass(generateAdapter = true)
data class Message(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class ChatResponse(
    val choices: List<Choice>
)

@JsonClass(generateAdapter = true)
data class Choice(
    val message: Message
)