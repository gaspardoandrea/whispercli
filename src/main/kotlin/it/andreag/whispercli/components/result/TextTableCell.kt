package it.andreag.whispercli.components.result

import it.andreag.whispercli.model.ParsedLine
import javafx.scene.control.TableCell
import javafx.scene.text.Text


class TextTableCell : TableCell<ParsedLine, String>() {
    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty);
        if (isEmpty()) return
        val text = Text(item.toString())
        text.wrappingWidthProperty().bind(widthProperty())

        isWrapText = true
        graphic = text
    }
}