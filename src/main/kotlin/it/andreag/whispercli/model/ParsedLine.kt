package it.andreag.whispercli.model

import java.time.LocalTime

class ParsedLine(line: String) {
    var from: LocalTime
    var to: LocalTime
    var text: String
    var valid: Boolean

    init {
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
    }
}