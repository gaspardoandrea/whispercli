package it.andreag.whispercli.model

enum class AudioFileStatus {
    New, Transcribing, Transcribed;

    fun toIcon(): String {
        return when(this) {
            New -> "mdi2f-format-clear"
            Transcribing -> "mdi2b-book-open-page-variant-outline"
            Transcribed -> "mdi2c-check-circle"
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