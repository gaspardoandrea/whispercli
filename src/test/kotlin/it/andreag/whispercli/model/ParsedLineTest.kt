package it.andreag.whispercli.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalTime

class ParsedLineTest {
    @Test
    fun appendHours() {
        assertEquals(ParsedLine.appendHours("00:00.123"), "00:00:00.123")
        assertEquals(ParsedLine.appendHours("00:00:00.123"), "00:00:00.123")
    }

    @Test
    fun timeFromMs() {
        assertEquals(ParsedLine.timeFromMs("16500.0 ms"), LocalTime.parse("00:00:16.5"))
        assertEquals(ParsedLine.timeFromMs("15500.5 ms"), LocalTime.parse("00:00:15.5005"))
    }

    @Test
    fun matchResult() {
        val l =
            ParsedLine("[00:00.000 --> 00:11.040]  e ci ha raccontato del suo esperienza pressionale di perch? nasce l'associazione Atlantidee")
        assertEquals(l.from, LocalTime.parse("00:00:00.000"))
        assertEquals(l.to, LocalTime.parse("00:00:11.040"))
        assertEquals(
            l.text,
            "e ci ha raccontato del suo esperienza pressionale di perch? nasce l'associazione Atlantidee"
        )
    }
}