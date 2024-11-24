package it.andreag.whispercli.model

import javafx.util.Duration
import java.time.LocalTime

class ParsedLine {
    var from: LocalTime
    var to: LocalTime
    var text: String
    var valid: Boolean
    var audioFile: AudioFile
    var audioLine: AudioLine?

    private constructor(line: String, audioFile: AudioFile) {
        val seq = matchResult(line)
        this.audioFile = audioFile
        try {
            from = LocalTime.parse("00:" + seq.elementAt(0).value)
            to = LocalTime.parse("00:" + seq.elementAt(1).value)
            text = line.split("]  ").elementAt(1).trim()
            valid = true
            audioLine = null
        } catch (_: Exception) {
            from = LocalTime.parse("00:00:00")
            to = LocalTime.parse("00:00:00")
            text = ""
            valid = false
            audioLine = null
        }
    }

    private constructor(line: AudioLine, audioFile: AudioFile) {
        this.audioFile = audioFile
        from = localTimeFromSec(line.start)
        to = localTimeFromSec(line.end)
        text = line.text
        valid = true
        audioLine = line
    }

    private fun localTimeFromSec(secs: Double): LocalTime {
        val t = LocalTime.of(0, 0, 0, 0)

        return t.plusSeconds(secs.toLong())
    }

    private fun matchResult(line: String) = Regex("([0-9][0-9]:[0-9][0-9].[0-9][0-9][0-9])").findAll(line)

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

        fun fromConsole(text: String, audioFile: AudioFile): ParsedLine {
            return ParsedLine(text, audioFile)
        }

        fun fromAudioLine(audioLine: AudioLine, audioFile: AudioFile): ParsedLine {
            return ParsedLine(audioLine, audioFile)
        }
    }

    fun getFromDuration(): Duration {
        return Duration(audioLine?.start?.toDouble()?.times(1000) ?: 0.0)
    }
}