package it.andreag.whispercli.components

import it.andreag.whispercli.model.AudioFile
import javafx.scene.control.ScrollPane

class ResultPanel : AudioFilePanel() {
    private val table: ResultTable = ResultTable()
    private val scroller: ScrollPane = ScrollPane(table)

    init {
        center = scroller
        setMargin(scroller, BigInsets())
    }

    override fun updateContent(transcriptionModel: String, audioFile: AudioFile) {
        table.dataList.clear()
        audioFile.loadParsedLines(transcriptionModel)
        table.dataList.addAll(audioFile.parsedLines)
    }
}