package it.andreag.whispercli.model

import javafx.util.Duration
import java.time.LocalTime

class SourceParsedLine : ParsedLine {
    var valid: Boolean
    var audioLine: AudioLine?

    private constructor(line: String, audioFile: AudioFile) : super(line, audioFile) {
        this.audioFile = audioFile
        try {
            valid = true
            audioLine = null
        } catch (_: Exception) {
            valid = false
            audioLine = null
        }
    }

    private constructor(line: AudioLine, audioFile: AudioFile) : super(audioFile) {
        this.audioFile = audioFile
        from = localTimeFromSec(line.start)
        to = localTimeFromSec(line.end)
        text = line.text
        valid = true
        audioLine = line
    }

    companion object {
        fun appendHours(line: String): String {
            return if (line.count { it == ':' } == 1) {
                "00:$line"
            } else {
                line
            }
        }

        fun timeFromMs(ms: String): LocalTime {
            return LocalTime.parse("00:00:00").plusNanos((ms.replace(" ms", "").toDouble() * 1000000).toLong())
        }

        fun fromConsole(text: String, audioFile: AudioFile): SourceParsedLine {
            return SourceParsedLine(text, audioFile)
        }

        fun fromAudioLine(audioLine: AudioLine, audioFile: AudioFile): SourceParsedLine {
            return SourceParsedLine(audioLine, audioFile)
        }
    }
}