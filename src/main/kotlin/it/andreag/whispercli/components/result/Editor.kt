package it.andreag.whispercli.components.result

import it.andreag.whispercli.model.AudioFile
import it.andreag.whispercli.model.ParsedLine
import it.andreag.whispercli.model.SerializableRow
import it.andreag.whispercli.service.AppPreferences
import it.andreag.whispercli.service.MediaPlayerManager
import javafx.beans.value.ChangeListener
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.util.Callback
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
    private val joinRowsButton: Button = Button()
    private val resetButton: Button = Button()
    private val transcriptionDuration: Label = Label()

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

        val toWordIcon = FontIcon()
        toWordIcon.iconLiteral = bundle?.getString("iconToWord")
        toWordIcon.iconSize = 24
        toWordButton.tooltip = Tooltip(bundle?.getString("toWord"))
        toWordButton.graphic = toWordIcon

        val resetIcon = FontIcon()
        resetIcon.iconLiteral = bundle?.getString("iconReset")
        resetIcon.iconSize = 24
        resetButton.tooltip = Tooltip(bundle?.getString("resetEditor"))
        resetButton.graphic = resetIcon

        val joinRowsIcon = FontIcon()
        joinRowsIcon.iconLiteral = bundle?.getString("iconJoinRows")
        joinRowsIcon.iconSize = 24
        joinRowsButton.tooltip = Tooltip(bundle?.getString("joinRows"))
        joinRowsButton.graphic = joinRowsIcon
        joinRowsButton.isDisable = true

        toolbar.items.add(saveButton)
        toolbar.items.add(toWordButton)
        toolbar.items.add(joinRowsButton)
        toolbar.items.add(resetButton)
        toolbar.items.add(transcriptionDuration)

        top = toolbar
        textArea.isWrapText = true
        center = textArea
        addListeners()
    }

    fun addListeners() {
        textArea.textProperty().addListener(ChangeListener { observable, oldValue, newValue ->
            saveButton.isDisable = false
        })

        textArea.onKeyPressed = EventHandler<KeyEvent> { event: KeyEvent ->
            if (event.isControlDown && event.text == "j") {
                joinRows()
            } else if (event.isControlDown && event.text == "s") {
                save()
            } else if (event.isControlDown && event.text == "w") {
                openInEditor()
            } else if (event.isControlDown && event.text == "r") {
                resetEditor()
            }
        }

        saveButton.onAction = EventHandler<ActionEvent> {
            save()
        }
        resetButton.onAction = EventHandler<ActionEvent> {
            resetEditor()
        }
        toWordButton.onAction = EventHandler<ActionEvent> {
            openInEditor()
        }
        joinRowsButton.onMousePressed = EventHandler<MouseEvent> {
            joinRows()
        }

        textArea.focusedProperty().addListener(ChangeListener { observable, oldValue, newValue ->
            recalcJoinRowButton()
        })

        textArea.caretPositionProperty().addListener(ChangeListener { observable, oldValue, newValue ->
            recalcJoinRowButton()
            if (!AppPreferences.getInstance().autoPlayRow()) {
                return@ChangeListener
            }
            val newRow = getRowFromCaretPosition(newValue)
            if (newRow == null) {
                return@ChangeListener
            }
            val newParsedLine = ParsedLine.fromSerialized(newRow, audioFile!!)
            if (currentRow != null && currentRow?.equalsTo(newParsedLine) == true) {
                return@ChangeListener
            }
            currentRow = newParsedLine

            audioFile?.onMedia {
                MediaPlayerManager.getInstance().play(newParsedLine)
            }
        })
    }

    private fun resetEditor() {
        val title = bundle?.getString("confirm")
        val message = bundle?.getString("confirmReset")

        if (!showConfirmDialog(title, message)) {
            return
        }

//        if (JOptionPane.showConfirmDialog(
//                null, bundle?.getString("confirmReset"),
//                bundle?.getString("confirm"),
//                JOptionPane.YES_NO_OPTION
//            ) == JOptionPane.NO_OPTION
//        ) {
//            return
//        }
        updateEditorContent(audioFile!!, AppPreferences.getInstance().getTranscriptionModel(), true)
    }

    private fun showConfirmDialog(title: String?, content: String?): Boolean {
        val dialog = Dialog<Boolean?>()
        dialog.title = title
        dialog.contentText = content
        dialog.dialogPane.buttonTypes.add(ButtonType.YES)
        dialog.dialogPane.buttonTypes.add(ButtonType.NO)
        dialog.resultConverter = Callback { buttonType: ButtonType? ->
            return@Callback buttonType == ButtonType.YES
        }
        val result: Boolean = dialog.showAndWait().orElse(false) == true
        return result
    }

    private fun recalcJoinRowButton() {
        joinRowsButton.isDisable = textArea.text.isEmpty() || !textArea.isFocused
    }

    private fun joinRows() {
        var text = textArea.text
        val from = getRowFromCaretPosition(textArea.selection.start)
        if (from == null) {
            return
        }

        var to = getRowFromCaretPosition(textArea.selection.end)
        if (to == null) {
            return
        }

        if (from == to) {
            to = getNextRow(to)
        }
        if (to == null || from == to) {
            return
        }
        val fromString = from.getEditorString()
        val toString = to.getEditorString()

        var startIndex = text.indexOf(fromString)
        var toIndex = text.indexOf(toString) + toString.length

        val rowsText = text.substring(startIndex, toIndex)

        val pattern: Pattern = Pattern.compile("\\d+:\\d+:\\d+\\.\\d+ \\d+:\\d+:\\d+\\.\\d+")
        val strings = pattern.splitWithDelimiters(rowsText, -1)
        val rv: ArrayList<SerializableRow> = ArrayList<SerializableRow>()

        var rangeFrom: String? = null
        var rangeTo: String? = null
        strings.forEach {
            if (it.isEmpty()) {
                return@forEach
            }
            if (rangeFrom == null || rangeTo == null) {
                var fromTo = it.split(" ")
                rangeFrom = fromTo[0]
                rangeTo = fromTo[1]
            } else {
                rv.add(SerializableRow(rangeFrom, rangeTo, it.trim()))
                rangeFrom = null
                rangeTo = null
            }
        }

        val newText = mergeRows(rv).getEditorString()
        textArea.replaceText(startIndex, toIndex, newText)
    }

    private fun mergeRows(rows: ArrayList<SerializableRow>): SerializableRow {
        return SerializableRow(rows.first().from, rows.last().to, mergeRowsText(rows).trim())
    }

    private fun mergeRowsText(rows: ArrayList<SerializableRow>): String {
        var rv = ""
        rows.forEach { rv += " " + it.text }
        return rv.trim()

    }

    private fun getNextRow(row: SerializableRow): SerializableRow? {
        var text = textArea.text
        var editorString = row.getEditorString()
        var toIndex = text.indexOf(editorString) + editorString.length
        editorString = text.substring(toIndex + 1)

        val pattern: Pattern = Pattern.compile("\\d+:\\d+:\\d+\\.\\d+ \\d+:\\d+:\\d+\\.\\d+")
        val matcher: Matcher = pattern.matcher(editorString)
        if (!matcher.find()) {
            return null
        }
        val startTime = matcher.group()
        var rvText: String? = null
        if (!matcher.find()) {
            rvText = editorString
        } else {
            val endTime = matcher.group()
            rvText = editorString.substring(0, editorString.indexOf(endTime) - 1)
        }
        val fromTo = startTime.split(" ")

        return SerializableRow(
            fromTo[0],
            fromTo[1],
            rvText.substring(rvText.indexOf(fromTo[1]) + fromTo[1].length + 1).trim()
        )
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

    fun save() {
        audioFile?.saveFromEditor(textArea.text)
        saveButton.isDisable = true
    }

    private fun getRowFromCaretPosition(caretPosition: Number): SerializableRow? {
        if (textArea.text.isEmpty()) return null
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

    fun updateEditorContent(audioFile: AudioFile, transcriptionModel: String, forceReload: Boolean) {
        val stringBuilder = StringBuilder()
        audioFile.loadParsedLines(transcriptionModel, forceReload)
        lines = audioFile.editedLines
        lines?.forEach { stringBuilder.appendLine(it.toEditorString()) }
        textArea.text = stringBuilder.toString()
        this.audioFile = audioFile
        saveButton.isDisable = true
        transcriptionDuration.text = audioFile.getFormattedProcTime()
    }

    fun needSave(): Boolean {
        return !saveButton.isDisable
    }
}