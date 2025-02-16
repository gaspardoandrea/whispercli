package it.andreag.whispercli.model

class TableAudioLine(pl: SourceParsedLine) {
    fun getValueToShow(): String? {
        return if (updatedText == null) {
            audioLine.text.trim()
        } else {
            updatedText!!.trim()
        }
    }

    var updatedText: String? = null
    val audioLine: SourceParsedLine = pl
}