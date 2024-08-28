package it.andreag.whispercli.components

import it.andreag.whispercli.model.AudioFile
import it.andreag.whispercli.service.MediaPlayerManager

class ResultPanel : AudioFilePanel() {
    private val table: ResultTable = ResultTable()

    init {
        center = table
        setMargin(table, BigInsets())
        table.selectionModel.selectedItemProperty().addListener({ obs, oldSelection, newSelection ->
            if (newSelection == null) {
                return@addListener
            }
            newSelection.audioFile.onMedia {
                MediaPlayerManager.getInstance().play(newSelection)
            }
        })
    }

    override fun updateContent(transcriptionModel: String, audioFile: AudioFile) {
        table.dataList.clear()
        audioFile.loadParsedLines(transcriptionModel)
        table.dataList.addAll(audioFile.parsedLines)
    }
}