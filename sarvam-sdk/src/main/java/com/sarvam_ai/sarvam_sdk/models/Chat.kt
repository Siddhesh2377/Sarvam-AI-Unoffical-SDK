package com.sarvam_ai.sarvam_sdk.models

/**
 * Supported chat models for Sarvam AI.
 *
 * Use these to specify which chat/completion model to use in API requests.
 */
enum class Chat(val modelId: String) {

    /** Default Sarvam-Medium model for chat/completions. */
    SARVAM_M("sarvam-m");
}
