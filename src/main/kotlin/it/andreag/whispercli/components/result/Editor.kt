package it.andreag.whispercli.components.result

import it.andreag.whispercli.model.AudioFile
import it.andreag.whispercli.model.ParsedLine
import it.andreag.whispercli.model.SerializableRow
import it.andreag.whispercli.service.AppPreferences
import it.andreag.whispercli.service.MediaPlayerManager
import javafx.beans.value.ChangeListener
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.ToolBar
import javafx.scene.control.Tooltip
import javafx.scene.layout.BorderPane
import org.kordamp.ikonli.javafx.FontIcon
import java.awt.Desktop
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.max


class Editor : BorderPane {
    private val textArea: TextArea = TextArea()
    private val saveButton: Button = Button()
    private val toWordButton: Button = Button()
    private val bundle: ResourceBundle? = ResourceBundle.getBundle("it.andreag.whispercli.bundle")
    private var currentRow: ParsedLine? = null
    private var lines: ArrayList<ParsedLine>? = null
    private var audioFile: AudioFile? = null

    constructor() : super() {
        val toolbar = ToolBar()

        val saveIcon = FontIcon()
        saveIcon.iconLiteral = bundle?.getString("iconSave")
        saveIcon.iconSize = 24
        saveButton.isDisable = true
        saveButton.tooltip = Tooltip(bundle?.getString("save"))
        saveButton.graphic = saveIcon

        val toWord = FontIcon()
        toWord.iconLiteral = bundle?.getString("iconToWord")
        toWord.iconSize = 24
        toWordButton.tooltip = Tooltip(bundle?.getString("toWord"))
        toWordButton.graphic = toWord

        toolbar.items.add(saveButton)
        toolbar.items.add(toWordButton)

        top = toolbar
        textArea.isWrapText = true
        center = textArea
        addListeners()
    }

    fun addListeners() {
        textArea.textProperty().addListener(ChangeListener { observable, oldValue, newValue ->
            saveButton.isDisable = false
        })

        saveButton.onAction = EventHandler<javafx.event.ActionEvent> {
            saveEditor()
        }
        toWordButton.onAction = EventHandler<javafx.event.ActionEvent> {
            openInEditor()
        }

        textArea.caretPositionProperty().addListener(ChangeListener { observable, oldValue, newValue ->
            val newRow = getRowFromCaretPosition(newValue)
            if (!AppPreferences.getInstance().autoPlayRow()) {
                return@ChangeListener
            }
            val newParsedLine = ParsedLine.fromSerialized(newRow, audioFile!!)
            if (currentRow != null && currentRow?.equalsTo(newParsedLine) ?: false) {
                return@ChangeListener
            }
            currentRow = newParsedLine

            audioFile?.onMedia {
                MediaPlayerManager.getInstance().play(newParsedLine)
            }
        })
    }

    private fun openInEditor() {
        val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
        if (desktop != null && desktop.isSupported(Desktop.Action.OPEN)) {
            audioFile?.saveFromEditor(textArea.text)
            desktop.open(audioFile!!.getEditedTextFile(AppPreferences.getInstance().getTranscriptionModel()))
        } else {
            throw UnsupportedOperationException("Open action not supported")
        }
    }

    private fun saveEditor() {
        audioFile?.saveFromEditor(textArea.text)
        saveButton.isDisable = true
    }

    private fun getRowFromCaretPosition(caretPosition: Number): SerializableRow {
        val allText = textArea.text
        val nextText = allText.substring(max(2, caretPosition.toInt()))
        val pattern: Pattern = Pattern.compile("\\d+:\\d+:\\d+\\.\\d+ \\d+:\\d+:\\d+\\.\\d+")
        val matcher: Matcher = pattern.matcher(nextText)
        var limit: String? = null
        if (matcher.find()) {
            val pos = allText.indexOf(matcher.group())
            limit = allText.substring(0, pos)
        } else {
            limit = allText
        }

        val strings = pattern.splitWithDelimiters(limit, -1)
        val fromTo = strings[strings.size - 2].split(" ")
        return SerializableRow(fromTo[0], fromTo[1], strings.last())
    }

    fun updateEditorContent(audioFile: AudioFile, transcriptionModel: String) {
        val stringBuilder = StringBuilder()
        audioFile.loadParsedLines(transcriptionModel)
        lines = audioFile.editedLines
        lines?.forEach { stringBuilder.appendLine(it.toEditorString()) }
        textArea.text = stringBuilder.toString()
        this.audioFile = audioFile
        saveButton.isDisable = true
    }
}
