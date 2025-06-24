package com.sarvam_ai.sarvam_sdk.api.stt

import android.media.MediaRecorder
import android.util.Log
import com.sarvam_ai.sarvam_sdk.api.api_interfaces.STTApi
import com.sarvam_ai.sarvam_sdk.models.Language
import com.sarvam_ai.sarvam_sdk.models.STTModels
import com.sarvam_ai.sarvam_sdk.models.STTModels.SAARIKA_V2
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

/**
 * STTClient handles Speech-to-Text operations with Sarvam AI.
 * Supports audio recording and transcription with dynamic parameters.
 */
object STTClient {

    private var apiKey: String = ""
    private var recorder: MediaRecorder? = null

    private val client by lazy {
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

    private val api by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.sarvam.ai/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(STTApi::class.java)
    }

    /**
     * Initializes the STTClient with your API key.
     */
    fun init(key: String) {
        apiKey = key.trim()
        Log.d("STTClient", "API key initialized.")
    }

    /**
     * Starts audio recording to the provided file.
     */
    fun startRecording(outputFile: File) {
        stopRecording()
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)
            prepare()
            start()
        }
        Log.d("STTClient", "Recording started: ${outputFile.absolutePath}")
    }

    /**
     * Stops recording and releases resources.
     */
    fun stopRecording() {
        try {
            recorder?.apply {
                stop()
                release()
                Log.d("STTClient", "Recording stopped.")
            }
        } catch (e: Exception) {
            Log.e("STTClient", "Error stopping recording: ${e.message}", e)
        } finally {
            recorder = null
        }
    }

    /**
     * Transcribes an audio file using Sarvam STT.
     *
     * @param audioFile The audio file to transcribe.
     * @param language Optional language code (e.g., "en-IN").
     * @param modelId Optional STT model ID. Defaults to Saarika v2.
     * @param onCompletion Called with the transcribed text.
     * @param onError Called on error.
     */
    suspend fun transcribe(
        audioFile: File,
        language: Language? = Language.ENGLISH,
        modelId: STTModels = SAARIKA_V2,
        onCompletion: (String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        try {
            val filePart = MultipartBody.Part.createFormData(
                "file",
                audioFile.name,
                audioFile.asRequestBody("audio/wav".toMediaTypeOrNull())
            )

            val response = api.transcribeAudio(
                file = filePart,
                model = modelId.asRequestBody(),
                languageCode = language?.code
            )

            if (response.isSuccessful) {
                onCompletion(response.body()?.transcript.orEmpty())
            } else {
                onError(Throwable("Error ${response.code()}: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            onError(e)
        }
    }
}
