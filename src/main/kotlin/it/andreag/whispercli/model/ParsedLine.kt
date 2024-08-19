package it.andreag.whispercli.model

import java.time.LocalTime
import java.time.temporal.TemporalUnit
import kotlin.time.Duration.Companion.seconds

class ParsedLine {
    var from: LocalTime
    var to: LocalTime
    var text: String
    var valid: Boolean

    private constructor(line: String) {
        val seq = matchResult(line)
        try {
            from = LocalTime.parse("00:" + seq.elementAt(0).value)
            to = LocalTime.parse("00:" + seq.elementAt(1).value)
            text = line.split("]  ").elementAt(1).trim()
            valid = true
        } catch (e: Exception) {
            from = LocalTime.parse("00:00:00")
            to = LocalTime.parse("00:00:00")
            text = ""
            valid = false
        }
    }

    private constructor(line: AudioLine) {
        from = localTimeFromSec(line.start)
        to = localTimeFromSec(line.end)
        text = line.text
        valid = true
    }

    private fun localTimeFromSec(secs: Double): LocalTime {
        var t = LocalTime.of(0, 0, 0, 0)
        t.plusSeconds(secs.toLong())

        return t
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

        fun fromConsole(text: String): ParsedLine {
            return ParsedLine(text)
        }

        fun fromAudioLine(audioLine: AudioLine): ParsedLine {
            return ParsedLine(audioLine)
        }
    }
}