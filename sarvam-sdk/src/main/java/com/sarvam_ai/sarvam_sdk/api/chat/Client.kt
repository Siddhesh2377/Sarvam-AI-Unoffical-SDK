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

object ApiClient {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.API_KEY}")
                .build()
                .let(chain::proceed)
        }
        .build()

    /**
     * Streaming chat using OkHttp-SSE
     */
    fun streamChat(
        request: ChatRequest,
        onTokenReceived: (String) -> Unit,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        // 1) dump outgoing JSON
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

                    // parse the chunk
                    val delta = JSONObject(data)
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("delta")

                    // safely extract content, ignoring JSON-null and empty strings
                    val token = delta.optString("content")
                    if (delta.isNull("content")) return

                    if (!token.isNullOrEmpty()) {
                        onTokenReceived(token)
                    }
                }


                override fun onFailure(source: EventSource, t: Throwable?, response: Response?) {
                    onError(t ?: Throwable("Unknown SSE error"))
                    source.cancel()
                }
            })
    }
}
