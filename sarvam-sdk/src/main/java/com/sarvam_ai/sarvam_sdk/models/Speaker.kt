package com.sarvam_ai.sarvam_sdk.models

/**
 * Represents available Sarvam AI voice speakers.
 *
 * Each speaker corresponds to a distinct voice model provided by Sarvam AI.
 * These can be used for Text-to-Speech (TTS) generation via Sarvam's API.
 *
 * @property id The unique identifier string for the speaker, used in API requests.
 */
enum class Speaker(val id: String) {

    /** Female voice: Anushka (Indian accent) */
    ANUSHKA("anushka"),

    /** Male voice: Abhilash (Indian accent) */
    ABHILASH("abhilash"),

    /** Female voice: Manisha (Indian accent) */
    MANISHA("manisha"),

    /** Female voice: Vidya (Indian accent) */
    VIDYA("vidya"),

    /** Female voice: Arya (Indian accent) */
    ARYA("arya"),

    /** Male voice: Karun (Indian accent) */
    KARUN("karun"),

    /** Male voice: Hitesh (Indian accent) */
    HITESH("hitesh"),

    NONE("None")
}
