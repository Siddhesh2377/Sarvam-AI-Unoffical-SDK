package com.sarvam_ai.sarvam_sdk.api.api_interfaces

import com.sarvam_ai.sarvam_sdk.models.Language
import com.squareup.moshi.JsonClass
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API interface for Sarvam AI Text-to-Speech (TTS) service.
 */
interface TTSApi {

    /**
     * Converts text into speech audio using Sarvam AI with dynamic multipart parameters.
     *
     * @param text Text to convert to speech.
     * @param model Model identifier for TTS engine.
     * @param languageCode Optional ISO language code (e.g., "en-IN").
     * @param speaker Optional speaker voice preset.
     * @param pitch Optional pitch adjustment.
     * @param pace Optional speech speed multiplier.
     * @param loudness Optional loudness multiplier.
     * @param sampleRate Optional output sample rate in Hz.
     * @param enablePreprocessing If true, applies text preprocessing.
     * @return [TTSResponse] containing audio URLs or data.
     */
    @POST("text-to-speech")
    suspend fun textToSpeech(
        @Header("api-subscription-key") apiKey: String,
        @Body body: TTSRequest
    ): Response<TTSResponse>


    /**
     * Request body for Sarvam AI Text-to-Speech API.
     */
    @JsonClass(generateAdapter = true)
    data class TTSRequest(
        val text: String,
        val model: String,
        val target_language_code: RequestBody? = Language.ENGLISH.code,
        val speaker: String? = null,
        val pitch: Double? = null,
        val pace: Double? = null,
        val loudness: Double? = null,
        val speech_sample_rate: Int? = null,
        val enable_preprocessing: Boolean? = null
    )
}

/**
 * Represents the response from the Text-to-Speech API.
 *
 * @param audios List of generated audio outputs (URLs or encoded strings).
 * @param request_id Optional identifier for tracking requests.
 */
@JsonClass(generateAdapter = true)
data class TTSResponse(
    val audios: List<String>,
    val request_id: String?
)
