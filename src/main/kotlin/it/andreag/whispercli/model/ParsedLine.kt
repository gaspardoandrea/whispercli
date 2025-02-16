package it.andreag.whispercli.model

import javafx.util.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

open class ParsedLine {
    var from: LocalTime
    var to: LocalTime
    var text: String
    var audioFile: AudioFile

    protected constructor(audioFile: AudioFile) {
        from = LocalTime.parse("00:00:00")
        to = LocalTime.parse("00:00:00")
        text = ""
        this.audioFile = audioFile
    }

    private constructor(sourceParsedLine: SourceParsedLine) {
        this.from = sourceParsedLine.from
        this.to = sourceParsedLine.to
        this.text = sourceParsedLine.text.trim()
        this.audioFile = sourceParsedLine.audioFile
    }

    private constructor(serializableRow: SerializableRow, audioFile: AudioFile) {
        this.from = LocalTime.parse(serializableRow.from)
        this.to = LocalTime.parse(serializableRow.to)
        this.text = serializableRow.text
        this.audioFile = audioFile
    }

    protected constructor(line: String, audioFile: AudioFile) {
        val seq = matchResult(line)
        this.audioFile = audioFile
        try {
            from = LocalTime.parse("00:" + seq.elementAt(0).value)
            to = LocalTime.parse("00:" + seq.elementAt(1).value)
            text = line.split("]  ").elementAt(1).trim()
        } catch (_: Exception) {
            from = LocalTime.parse("00:00:00")
            to = LocalTime.parse("00:00:00")
            text = ""
        }
    }

    protected fun localTimeFromSec(secs: Double): LocalTime {
        val t = LocalTime.of(0, 0, 0, 0)

        return t.plusNanos(secs.times(1000000000).toLong())
    }

    protected fun matchResult(line: String) = Regex("([0-9][0-9]:[0-9][0-9].[0-9][0-9][0-9])").findAll(line)

    companion object {
        fun fromSourceParsedLine(line: SourceParsedLine): ParsedLine {
            return ParsedLine(line)
        }

        fun fromSerialized(row: SerializableRow, audioFile: AudioFile): ParsedLine {
            return ParsedLine(row, audioFile)
        }
    }

    fun toEditorString(): String {
        val dtf = DateTimeFormatter.ofPattern("HH:mm:ss.S")
        return from.format(dtf) + " " + to.format(dtf) + " " + text
    }

    fun getLineDuration(): Long {
        // FIXME c'è qualcosa che no va qui
        val ltTo = localTimeToMillis(to)
        val ltFrom = localTimeToMillis(from)
        return ltTo - ltFrom + 1200
    }

    private fun localTimeToMillis(lt: LocalTime): Long {
        return (lt.hour * 3600 + lt.minute * 60 + lt.second) * 1000 + (lt.nano / 1000000).toLong()
    }

    fun getFromDuration(): Duration {
        return Duration(localTimeToMillis(from).toDouble())
    }

    fun equalsTo(line: ParsedLine): Boolean {
        return line.text == text
    }
}