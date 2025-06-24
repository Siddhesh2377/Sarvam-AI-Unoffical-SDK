package com.sarvam_ai.sarvam_sdk.api.api_interfaces

import com.squareup.moshi.JsonClass
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API interface for Sarvam AI Speech-to-Text (STT) service.
 */
interface STTApi {

    /**
     * Transcribes an audio file into text using Sarvam AI.
     *
     * @param file The audio file to transcribe, sent as multipart/form-data.
     * @param model The model identifier for the STT engine.
     * @param languageCode Optional ISO language code (e.g., "en-IN"). If null, auto-detection may apply.
     * @return [STTResponse] containing the transcribed text.
     */
    @Multipart
    @POST("speech-to-text")
    suspend fun transcribeAudio(
        @Part file: MultipartBody.Part,
        @Part("model") model: RequestBody,
        @Part("language_code") languageCode: RequestBody? = null
    ): Response<STTResponse>
}

/**
 * Represents the response from the Speech-to-Text API.
 *
 * @param transcript The transcribed text output from the audio file.
 */
@JsonClass(generateAdapter = true)
data class STTResponse(
    val transcript: String
)
