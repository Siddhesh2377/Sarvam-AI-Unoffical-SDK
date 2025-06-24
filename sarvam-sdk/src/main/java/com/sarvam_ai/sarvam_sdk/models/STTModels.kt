package com.sarvam_ai.sarvam_sdk.models

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Supported STT (Speech-to-Text) models for Sarvam AI.
 *
 * Provides both plain model identifiers and ready-to-use [RequestBody] for API calls.
 */
enum class STTModels(val modelId: String) {

    /** Saarika V2 model for general transcription tasks */
    SAARIKA_V2("saarika:v2"),

    /** Saarika V2.5 model for improved transcription accuracy */
    SAARIKA_V2_5("saarika:v2.5");

    /**
     * Returns this model as a [RequestBody] ready for API usage.
     */
    fun asRequestBody(): RequestBody =
        modelId.toRequestBody("text/plain".toMediaTypeOrNull())
}
