package com.sarvam_ai.sarvam_sdk.models

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Supported TTS (Text-to-Speech) models for Sarvam AI.
 *
 * Provides both plain model identifiers and ready-to-use [RequestBody] for API calls.
 */
enum class TTSModels(val modelId: String) {

    /** Bulbul V2 model for natural speech synthesis */
    BULBUL_V2("bulbul:v2");

    /**
     * Returns this model as a [RequestBody] ready for API usage.
     */
    fun asRequestBody(): RequestBody =
        modelId.toRequestBody("text/plain".toMediaTypeOrNull())
}
