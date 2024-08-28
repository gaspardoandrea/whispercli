package it.andreag.whispercli.components.result

import it.andreag.whispercli.model.ParsedLine
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableColumn

class TextTableColumn : TableColumn<ParsedLine, String>() {
    init {
        text = "Text"
        isResizable = false
        setCellValueFactory {
            SimpleStringProperty(it.value.text)
        }
        isSortable = false
    }
}