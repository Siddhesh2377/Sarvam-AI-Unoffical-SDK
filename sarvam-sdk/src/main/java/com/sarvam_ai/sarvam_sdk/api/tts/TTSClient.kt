package com.sarvam_ai.sarvam_sdk.api.tts

import android.content.Context
import android.media.MediaPlayer
import android.util.Base64
import com.sarvam_ai.sarvam_sdk.api.api_interfaces.TTSApi
import com.sarvam_ai.sarvam_sdk.api.api_interfaces.TTSApi.TTSRequest
import com.sarvam_ai.sarvam_sdk.api.api_interfaces.TTSResponse
import com.sarvam_ai.sarvam_sdk.models.Language
import com.sarvam_ai.sarvam_sdk.models.TTSModels
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

/**
 * TTSClient handles integration with Sarvam AI's Text-to-Speech API.
 * Provides easy audio generation and playback from text.
 */
object TTSClient {

    private lateinit var apiKey: String

    /**
     * Initializes the client with your Sarvam AI API key.
     */
    fun init(apiKey: String) {
        this.apiKey = apiKey
    }

    private val httpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val headerInterceptor = Interceptor { chain ->
            val req = chain.request().newBuilder()
                .addHeader("api-subscription-key", apiKey)
                .build()
            chain.proceed(req)
        }

        OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(logging)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.sarvam.ai/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }

    val api: TTSApi by lazy {
        retrofit.create(TTSApi::class.java)
    }

    /**
     * Generates TTS audio from text and returns the raw audio bytes.
     */
    suspend fun generate(
        text: String,
        modelId: TTSModels = TTSModels.BULBUL_V2,
        languageCode: Language? = Language.ENGLISH,
        speakerId: String? = null,
        pitch: Double? = null,
        pace: Double? = null,
        loudness: Double? = null,
        sampleRate: Int? = null,
        enablePreprocessing: Boolean? = null,
        onCompletion: (ByteArray) -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        try {
            val body = TTSRequest(
                text = text,
                target_language_code = languageCode?.code,
                speaker = speakerId,
                pitch = pitch,
                pace = pace,
                loudness = loudness,
                speech_sample_rate = sampleRate,
                enable_preprocessing = enablePreprocessing,
                model = modelId.modelId
            )

            val response = api.textToSpeech(apiKey, body)
            if (response.isSuccessful) {
                val body = response.body() ?: throw Throwable("Empty response")
                handleAudio(body, onCompletion, onError)
            } else {
                onError(Throwable("HTTP ${response.code()} ${response.message()}"))
            }

        } catch (e: Exception) {
            onError(e)
        }
    }

    private fun handleAudio(
        response: TTSResponse,
        onCompletion: (ByteArray) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        response.audios.firstOrNull()?.let { base64Audio ->
            val audioBytes = Base64.decode(base64Audio, Base64.DEFAULT)
            onCompletion(audioBytes)
        } ?: onError(Throwable("No audio data received"))
    }

    /**
     * Plays raw audio data using MediaPlayer. Saves audio to a temp file.
     */
    fun playAudio(
        data: ByteArray,
        context: Context,
        audioFile: File = File.createTempFile("sarvam_tts", "wav", context.cacheDir)
    ) {
        audioFile.writeBytes(data)
        MediaPlayer().apply {
            setDataSource(audioFile.absolutePath)
            prepare()
            start()
        }
    }
}