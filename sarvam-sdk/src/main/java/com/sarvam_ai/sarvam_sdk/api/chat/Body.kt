package com.sarvam_ai.sarvam_sdk.api.chat

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ChatRequest(
    val model: String = "sarvam-m",  // You can change this to "sarvam-0.5" if that's the model you want
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