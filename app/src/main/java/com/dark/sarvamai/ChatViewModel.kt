package com.dark.sarvamai

import android.util.Log
import androidx.lifecycle.ViewModel
import com.sarvam_ai.sarvam_sdk.api.chat.ApiClient.streamChat
import com.sarvam_ai.sarvam_sdk.api.chat.ChatRequest
import com.sarvam_ai.sarvam_sdk.api.chat.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(
        listOf(
            Message(
                role = "system",
                content = "You Are Madhav a Helpfull Assistant"

            )
        )
    )
    val messages = _messages.asStateFlow()

    fun sendMessage(userInput: String) {
        val newMessages = _messages.value + Message("user", userInput)
        _messages.value = newMessages + Message("assistant", "") // placeholder for streaming

        var contentBuffer = ""


        streamChat(
            request = ChatRequest(messages = newMessages),
            onTokenReceived = { token ->
                contentBuffer += token
                _messages.value = _messages.value.dropLast(1) + Message("assistant", contentBuffer)
                Log.d("ChatViewModel", "Received token: $token")
            },
            onComplete = {
                _messages.value = _messages.value.dropLast(1) + Message("assistant", contentBuffer)
                Log.d("ChatViewModel", "Stream completed")
            },
            onError = { error ->
                _messages.value += Message("assistant", "Error: ${error.message}")
                Log.e("ChatViewModel", "Error: ${error.message}", error)
            }
        )
    }
}
