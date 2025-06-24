package com.dark.sarvamai.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dark.sarvamai.utils.UserPrefs.getApiKey
import com.sarvam_ai.sarvam_sdk.api.chat.ChatClient
import com.sarvam_ai.sarvam_sdk.api.chat.ChatClient.init
import com.sarvam_ai.sarvam_sdk.api.stt.STTClient
import com.sarvam_ai.sarvam_sdk.api.tts.TTSClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConversionViewModel : ViewModel() {

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _message = MutableStateFlow("Tap On Mic To Start..!")
    val message = _message.asStateFlow()


    fun toggleRecording(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            init(getApiKey(context).toString())
            TTSClient.init(getApiKey(context).toString())
            STTClient.init(getApiKey(context).toString())
        }

        val tempFile = context.cacheDir.resolve("temp.wav")
        _isRecording.value = !_isRecording.value

        if (_isRecording.value) {
            STTClient.startRecording(tempFile)
            _message.value = "Listening..."
        } else {
            STTClient.stopRecording()
            viewModelScope.launch(Dispatchers.IO) {
                STTClient.transcribe(
                    tempFile,
                    onCompletion = { text ->
                        sendMessage(text, context)
                    },
                    onError = {
                        _message.value = "STT Error"
                    }
                )
            }
        }
    }

    private fun sendMessage(input: String, context: Context) {
        _message.value = "You: $input"
        ChatClient.chat(
            history = emptyList(),
            userInput = input,
            stream = true,
            onTokenReceived = {
                _message.value = _message.value + it
            },
            onComplete = { output, _ ->
                _message.value = output
                viewModelScope.launch {
                    TTSClient.generate(
                        output,
                        onCompletion = { TTSClient.playAudio(it, context) },
                        onError = { _message.value = "TTS Error" }
                    )
                }
            },
            onError = { _message.value = "Chat Error" }
        )
    }
}