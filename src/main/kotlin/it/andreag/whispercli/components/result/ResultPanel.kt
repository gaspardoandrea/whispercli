package it.andreag.whispercli.components.result

import it.andreag.whispercli.components.AudioFilePanel
import it.andreag.whispercli.components.insets.BigInsets
import it.andreag.whispercli.model.AudioFile
import it.andreag.whispercli.model.TableAudioLine
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
            newSelection.audioLine.audioFile.onMedia {
                MediaPlayerManager.getInstance().play(newSelection.audioLine)
            }
        })
    }

    override fun updateContent(transcriptionModel: String, audioFile: AudioFile) {
        table.dataList.clear()
        audioFile.loadParsedLines(transcriptionModel)
        val lines: ArrayList<TableAudioLine> = ArrayList()

        audioFile.parsedLines.forEach { lines.add(TableAudioLine(it)) }
        table.dataList.addAll(lines)
    }
}