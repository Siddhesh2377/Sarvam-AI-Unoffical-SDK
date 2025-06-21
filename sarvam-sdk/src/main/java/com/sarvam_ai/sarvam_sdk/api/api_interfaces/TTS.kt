package com.sarvam_ai.sarvam_sdk.api.api_interfaces

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SarvamApi {
    @POST("text-to-speech")
    suspend fun textToSpeech(
        @Header("api-subscription-key") apiKey: String,
        @Body body: TTSRequest
    ): TTSResponse
}

data class TTSRequest(
    val text: String,
    val target_language_code: String, // e.g., "hi-IN"
    val speaker: String? = "anushka",
    val pitch: Double? = 0.0,
    val pace: Double? = 1.0,
    val loudness: Double? = 1.0,
    val speech_sample_rate: Int? = 22050,
    val enable_preprocessing: Boolean? = false,
    val model: String? = "bulbul:v2"
)

data class TTSResponse(
    val audios: List<String>,
    val request_id: String?
)
