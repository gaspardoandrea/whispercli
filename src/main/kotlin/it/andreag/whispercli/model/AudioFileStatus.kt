package it.andreag.whispercli.model

import java.util.*

enum class AudioFileStatus {
    New, Transcribing, Transcribed;

    val bundle: ResourceBundle? = ResourceBundle.getBundle("it.andreag.whispercli.bundle")

    fun toIcon(): String {
        return when (this) {
            New -> bundle?.getString("iconStatusNew") ?: ""
            Transcribing -> bundle?.getString("iconStatusTranscribing") ?: ""
            Transcribed -> bundle?.getString("iconStatusTranscribed") ?: ""
        }
    }

    fun isNew(): Boolean {
        return this == New
    }

    fun isTranscribed(): Boolean {
        return this == Transcribed
    }

    fun isTranscribing(): Boolean {
        return this == Transcribing
    }
}