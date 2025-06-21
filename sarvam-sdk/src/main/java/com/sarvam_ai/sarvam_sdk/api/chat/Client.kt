package com.sarvam_ai.sarvam_sdk.api.chat

import android.util.Log
import com.sarvam_ai.sarvam_sdk.BuildConfig
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
 * ApiClient handles chat completions (streaming & non-streaming) against Sarvam AI.
 */
object ApiClient {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private var apiKey: String = ""

    /**
     * Initialize SDK with custom API key. Call before using streaming.
     */
    fun init(apiKey: String) {
        Log.d("ApiClient", "ApiClient initialized with key: $apiKey")
        this@ApiClient.apiKey = apiKey
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Accept", "text/event-stream")          // Request SSE stream
                .addHeader("Content-Type", "application/json")      // Ensure JSON body
                .build()
                .let(chain::proceed)
        }
        .build()

    /**
     * Streaming chat via SSE. Emits tokens in real-time.
     */
    fun streamChat(
        request: ChatRequest,
        onTokenReceived: (String) -> Unit,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val json = moshi.adapter(ChatRequest::class.java)
            .toJson(request.copy(stream = true))
        Log.d("ApiClient", "Outgoing JSON → $json")

        val body = json.toRequestBody("application/json".toMediaType())
        val httpReq = Request.Builder()
            .url("https://api.sarvam.ai/v1/chat/completions")
            .post(body)
            .build()

        EventSources.createFactory(client)
            .newEventSource(httpReq, object : EventSourceListener() {
                override fun onEvent(
                    source: EventSource,
                    id: String?,
                    type: String?,
                    data: String
                ) {
                    if (data == "[DONE]") {
                        onComplete()
                        source.cancel()
                        return
                    }

                    val delta = JSONObject(data)
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("delta")

                    // Only emit actual content
                    val token = delta.optString("content", "null")
                    if (delta.isNull("content")) return
                    if (!token.isNullOrBlank()) {
                        onTokenReceived(token)
                    }
                }

                override fun onFailure(
                    source: EventSource,
                    t: Throwable?,
                    response: Response?
                ) {
                    // Surface HTTP errors with body if available
                    if (t != null) {
                        onError(t)
                    } else if (response != null) {
                        val errBody = response.body?.string()
                        onError(
                            Throwable("HTTP ${response.code} ${response.message}" +
                                    (errBody?.let { ": $it" } ?: ""))
                        )
                    } else {
                        onError(Throwable("Unknown SSE error"))
                    }
                    source.cancel()
                }
            })
    }
}
