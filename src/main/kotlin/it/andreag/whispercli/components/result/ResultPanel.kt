package it.andreag.whispercli.components.result

import it.andreag.whispercli.components.AudioFilePanel
import it.andreag.whispercli.components.insets.BigInsets
import it.andreag.whispercli.model.AudioFile
import it.andreag.whispercli.model.TableAudioLine
import it.andreag.whispercli.service.AppPreferences
import it.andreag.whispercli.service.MediaPlayerManager
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TabPane.TabClosingPolicy

class ResultPanel : AudioFilePanel() {
//    private val table: ResultTable = ResultTable()
    private val editor: Editor = Editor()

    init {
//        val tabPane = TabPane()
//        tabPane.tabClosingPolicy = TabClosingPolicy.UNAVAILABLE
//        val tab1 = Tab()
//        val tab2 = Tab()
//        tab1.text = "Content"
//        tab2.text = "Editor"
//
//        tab1.content = table
//        tab2.content = editor
//
//        tabPane.tabs.addAll(tab1, tab2)
//        center = tabPane

//        setMargin(table, BigInsets())
//        table.selectionModel.selectedItemProperty().addListener({ obs, oldSelection, newSelection ->
//            if (newSelection == null) {
//                return@addListener
//            }
//            if (!AppPreferences.getInstance().autoPlayRow()) {
//                return@addListener
//            }
//            newSelection.audioLine.audioFile.onMedia {
//                MediaPlayerManager.getInstance().play(newSelection.audioLine)
//            }
//        })
        center = editor
    }

    override fun updateContent(transcriptionModel: String, audioFile: AudioFile) {
        updateTableContent(audioFile, transcriptionModel)
        updateEditorContent(audioFile, transcriptionModel)
    }

    private fun updateTableContent(audioFile: AudioFile, transcriptionModel: String) {
//        table.dataList.clear()
//        audioFile.loadParsedLines(transcriptionModel)
//        val lines: ArrayList<TableAudioLine> = ArrayList()
//
//        audioFile.sourceParsedLines.forEach { lines.add(TableAudioLine(it)) }
//        table.dataList.addAll(lines)
    }

    private fun updateEditorContent(audioFile: AudioFile, transcriptionModel: String) {
        editor.updateEditorContent(audioFile, transcriptionModel, false)
    }
}