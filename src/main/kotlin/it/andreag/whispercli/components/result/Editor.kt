package it.andreag.whispercli.components.result

import it.andreag.whispercli.model.AudioFile
import it.andreag.whispercli.model.ParsedLine
import it.andreag.whispercli.service.MediaPlayerManager
import javafx.beans.value.ChangeListener
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.ToolBar
import javafx.scene.control.Tooltip
import javafx.scene.layout.BorderPane
import org.kordamp.ikonli.javafx.FontIcon
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class Editor : BorderPane {
    private val textArea: TextArea = TextArea()
    private val saveButton: Button = Button()
    private val bundle: ResourceBundle? = ResourceBundle.getBundle("it.andreag.whispercli.bundle")
    private var currentRow: Int? = null
    private var parsedLines: ArrayList<ParsedLine>? = null
    private var audioFile: AudioFile? = null

    constructor() : super() {
        val toolbar = ToolBar()
        val saveIcon = FontIcon()
        saveIcon.iconLiteral = bundle?.getString("iconSave")
        saveIcon.iconSize = 24
        saveButton.isDisable = true

        toolbar.items.add(saveButton)
        saveButton.tooltip = Tooltip(bundle?.getString("save"))
        saveButton.graphic = saveIcon

        top = toolbar
        center = textArea
        addListeners()
    }

    fun addListeners() {
        textArea.caretPositionProperty().addListener(ChangeListener { observable, oldValue, newValue ->
            val newRow = getRowFromCaretPosition(newValue)
            if (currentRow != newRow) {
                currentRow = newRow
                if (currentRow != null) {
                    audioFile?.onMedia {
                        MediaPlayerManager.getInstance().play(parsedLines!![currentRow!! - 1])
                    }
                }
            }
        })
    }

    private fun getRowFromCaretPosition(number: Number): Int {
        val prevText = textArea.text.substring(0, number.toInt())
        val pattern: Pattern = Pattern.compile("\\d+:\\d+:\\d+\\.\\d+ \\d+:\\d+:\\d+\\.\\d+")
        val matcher: Matcher = pattern.matcher(prevText)
        var count = 0
        while (matcher.find()) {
            count++
        }
        return count
    }

    fun updateEditorContent(audioFile: AudioFile, transcriptionModel: String) {
        val stringBuilder = StringBuilder()
        audioFile.loadParsedLines(transcriptionModel)
        parsedLines = audioFile.parsedLines
        parsedLines?.forEach { stringBuilder.appendLine(it.toEditorString()) }
        textArea.text = stringBuilder.toString()
        this.audioFile = audioFile
    }
}
