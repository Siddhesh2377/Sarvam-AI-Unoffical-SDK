package com.sarvam_ai.sarvam_sdk.api.chat

import android.util.Log
import com.sarvam_ai.sarvam_sdk.api.api_interfaces.ChatRequest
import com.sarvam_ai.sarvam_sdk.api.api_interfaces.Message
import com.sarvam_ai.sarvam_sdk.api.api_interfaces.ROLE
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import org.json.JSONObject

/**
 * ChatClient handles chat completions (streaming and non-streaming) against Sarvam AI.
 *
 * Use [init] to set your API key before making requests.
 * Use [chat] to send messages to the API, supporting both streaming and non-streaming modes.
 */
object ChatClient {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private var apiKey: String = ""

    /**
     * Initialize the SDK with your API key. Must be called before using [chat].
     */
    fun init(apiKey: String) {
        this.apiKey = apiKey
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Accept", "text/event-stream")
                .addHeader("Content-Type", "application/json")
                .build()
                .let(chain::proceed)
        }
        .build()

    /**
     * Sends a chat message to Sarvam AI.
     *
     * @param history The chat history as a list of [Message].
     * @param userInput The latest user message to send.
     * @param stream If true, enables streaming response via SSE. If false, waits for full response.
     * @param onTokenReceived Called with each token (word/fragment) as it's received. Only used in streaming mode.
     * @param onComplete Called when full output is received (end of stream or full response).
     * @param onError Called if any error occurs during the request.
     */
    fun chat(
        history: List<Message>,
        userInput: String,
        stream: Boolean = true,
        onTokenReceived: (String) -> Unit = {},
        onComplete: (output: String, updatedMessages: List<Message>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val newMessages = history + Message(role = ROLE.USER.value, content = userInput)
        var contentBuffer = ""

        val json = moshi.adapter(ChatRequest::class.java)
            .toJson(ChatRequest(messages = newMessages, stream = stream))
        Log.d("ChatClient", "Outgoing JSON → $json")

        val body = json.toRequestBody("application/json".toMediaType())
        val httpReq = Request.Builder()
            .url("https://api.sarvam.ai/v1/chat/completions")
            .post(body)
            .build()

        if (stream) {
            val interimMessages = newMessages + Message(role = ROLE.ASSISTANT.value, content = "")
            onTokenReceived("")

            EventSources.createFactory(client)
                .newEventSource(httpReq, object : EventSourceListener() {
                    override fun onEvent(source: EventSource, id: String?, type: String?, data: String) {
                        if (data == "[DONE]") {
                            val finalMessages = interimMessages.dropLast(1) + Message(role = ROLE.ASSISTANT.value, content = contentBuffer)
                            onComplete(contentBuffer, finalMessages)
                            source.cancel()
                            return
                        }

                        val delta = JSONObject(data)
                            .getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("delta")

                        val token = delta.optString("content", "null")
                        if (delta.isNull("content")) return
                        if (!token.isNullOrBlank()) {
                            contentBuffer += token
                            onTokenReceived(token)
                        }
                    }

                    override fun onFailure(source: EventSource, t: Throwable?, response: Response?) {
                        if (t != null) {
                            onError(t)
                        } else if (response != null) {
                            val errBody = response.body?.string()
                            onError(Throwable("HTTP ${response.code} ${response.message}" + (errBody?.let { ": $it" } ?: "")))
                        } else {
                            onError(Throwable("Unknown SSE error"))
                        }
                        source.cancel()
                    }
                })

        } else {
            client.newCall(httpReq).execute().use { response ->
                if (!response.isSuccessful) {
                    onError(Throwable("HTTP ${response.code} ${response.message}"))
                    return
                }
                val bodyString = response.body?.string() ?: ""
                val obj = JSONObject(bodyString)
                val content = obj.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                val updatedMessages = newMessages + Message(role = ROLE.ASSISTANT.value, content = content)
                onComplete(content, updatedMessages)
            }
        }
    }
}
