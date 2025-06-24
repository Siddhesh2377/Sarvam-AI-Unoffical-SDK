package com.dark.sarvamai.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.sarvam_ai.sarvam_sdk.api.api_interfaces.Message
import com.sarvam_ai.sarvam_sdk.api.api_interfaces.ROLE
import com.sarvam_ai.sarvam_sdk.api.chat.ChatClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(
        listOf(
            Message(
                role = ROLE.SYSTEM.value,
                content = "You Are Madhav a Helpful Assistant"
            )
        )
    )
    val messages = _messages.asStateFlow()

    fun sendMessage(userInput: String, onCompletion: (output: String) -> Unit) {
        val newMessages = _messages.value + Message(ROLE.USER.value, userInput)
        var contentBuffer = ""

        // Add assistant placeholder for UI
        _messages.value = newMessages + Message(ROLE.ASSISTANT.value, "")

        ChatClient.chat(
            history = _messages.value,
            userInput = userInput,
            stream = true, // Set false for non-streaming behavior
            onTokenReceived = { token ->
                contentBuffer += token
                _messages.value =
                    _messages.value.dropLast(1) + Message(ROLE.ASSISTANT.value, contentBuffer)
                Log.d("ChatViewModel", "Received token: $token")
            },
            onComplete = { output, updatedMessages ->
                _messages.value = updatedMessages
                Log.d("ChatViewModel", "Stream completed")
                onCompletion(output)
            },
            onError = { error ->
                _messages.value = _messages.value.dropLast(1) + Message(
                    ROLE.ASSISTANT.value,
                    "Error: ${error.message}"
                )
                Log.e("ChatViewModel", "Error: ${error.message}", error)
            }
        )
    }
}