package com.sarvam_ai.sarvam_sdk.models

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Supported language codes for Sarvam AI STT and TTS.
 *
 * Use this to specify language for speech recognition or synthesis.
 * The `code` matches BCP-47 standard (e.g., "en-IN").
 */
enum class Language(val code: RequestBody) {

    /** Auto-detect or unspecified language */
    UNKNOWN("unknown".toRequestBody("text/plain".toMediaTypeOrNull())),

    /** Hindi (India) */
    HINDI("hi-IN".toRequestBody("text/plain".toMediaTypeOrNull())),

    /** Bengali (India) */
    BENGALI("bn-IN".toRequestBody("text/plain".toMediaTypeOrNull())),

    /** Kannada (India) */
    KANNADA("kn-IN".toRequestBody("text/plain".toMediaTypeOrNull())),

    /** Malayalam (India) */
    MALAYALAM("ml-IN".toRequestBody("text/plain".toMediaTypeOrNull())),

    /** Marathi (India) */
    MARATHI("mr-IN".toRequestBody("text/plain".toMediaTypeOrNull())),

    /** Odia (India) */
    ODIA("or-IN".toRequestBody("text/plain".toMediaTypeOrNull())),

    /** Punjabi (India) */
    PUNJABI("pa-IN".toRequestBody("text/plain".toMediaTypeOrNull())),

    /** Tamil (India) */
    TAMIL("ta-IN".toRequestBody("text/plain".toMediaTypeOrNull())),

    /** Telugu (India) */
    TELUGU("te-IN".toRequestBody("text/plain".toMediaTypeOrNull())),

    /** English (India) */
    ENGLISH("en-IN".toRequestBody("text/plain".toMediaTypeOrNull())),

    /** Gujarati (India) */
    GUJARATI("gu-IN".toRequestBody("text/plain".toMediaTypeOrNull()));
}
