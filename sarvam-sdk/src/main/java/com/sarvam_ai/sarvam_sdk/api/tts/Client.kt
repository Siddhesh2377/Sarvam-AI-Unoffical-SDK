package com.sarvam_ai.sarvam_sdk.api.tts

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.util.Base64
import com.sarvam_ai.sarvam_sdk.api.api_interfaces.SarvamApi
import com.sarvam_ai.sarvam_sdk.api.api_interfaces.TTSRequest
import com.sarvam_ai.sarvam_sdk.models.Speaker
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

@SuppressLint("StaticFieldLeak")
object TTSClient {

    private lateinit var context: Context
    private lateinit var apiKey: String

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // or BASIC/HEADERS if needed
    }

    fun init(context: Context, apiKey: String) {
        this.context = context
        this.apiKey = apiKey
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.sarvam.ai/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()

    val api: SarvamApi = retrofit.create(SarvamApi::class.java)

    suspend fun fetchAndPlayTTS(text: String, speaker: Speaker) {
        val request = TTSRequest(
            text = text,
            target_language_code = "en-IN",
            speaker = speaker.id,
            pitch = 0.0,
            pace = 1.2,
            loudness = 1.0,
            speech_sample_rate = 22050,
            enable_preprocessing = true,
            model = "bulbul:v2"
        )
        val response = api.textToSpeech(apiKey, request)
        response.audios.firstOrNull()?.let { base64Audio ->
            val audioBytes = Base64.decode(base64Audio, Base64.DEFAULT)
            playAudio(audioBytes)
        }
    }

    fun playAudio(data: ByteArray) {
        val temp = File.createTempFile("sarvam_tts", "wav", context.cacheDir)
        temp.writeBytes(data)
        MediaPlayer().apply {
            setDataSource(temp.absolutePath)
            prepare()
            start()
        }
    }
}
