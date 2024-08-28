package it.andreag.whispercli.components.result

import it.andreag.whispercli.model.TableAudioLine
import javafx.scene.control.ContentDisplay
import javafx.scene.control.TableCell
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.text.Text


class TextTableCell : TableCell<TableAudioLine, String>() {
    private var textField: TextField? = null

    override fun startEdit() {
        super.startEdit()

        if (textField == null) {
            createTextField()
        }

        graphic = textField
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        textField?.selectAll()
    }

    override fun cancelEdit() {
        super.cancelEdit()

        text = item.toString()
        contentDisplay = ContentDisplay.TEXT_ONLY
    }

    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)

        if (empty) {
            text = null
            graphic = textField

            return
        }

        if (!isEditing) {
            val text = Text(item.toString())
            text.wrappingWidthProperty().bind(widthProperty())
            isWrapText = true
            graphic = text
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
        } else {
            textField?.text = getString()
            graphic = textField
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
        }
    }

    private fun createTextField() {
        textField = TextField(getString())
        textField!!.minWidth = this.width - this.graphicTextGap * 2
        textField!!.setOnKeyPressed { t ->
            if (t.code === KeyCode.ENTER) {
                commitEdit(textField!!.text)
            } else if (t.code === KeyCode.ESCAPE) {
                cancelEdit()
            }
        }
    }

    private fun getString(): String {
        return if (item == null) "" else item
    }
}