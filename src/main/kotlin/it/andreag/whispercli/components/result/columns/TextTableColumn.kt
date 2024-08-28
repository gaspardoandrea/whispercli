package it.andreag.whispercli.components.result.columns

import it.andreag.whispercli.components.result.TextTableCell
import it.andreag.whispercli.model.TableAudioLine
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableColumn

class TextTableColumn : TableColumn<TableAudioLine, String>() {
    init {
        text = "Text"
        isResizable = false
        setCellValueFactory {
            SimpleStringProperty(it.value.getValueToShow())
        }
        isSortable = false

        setCellFactory {
            return@setCellFactory TextTableCell()
        }
    }
}