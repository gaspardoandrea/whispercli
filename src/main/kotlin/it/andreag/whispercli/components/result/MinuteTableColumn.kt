package it.andreag.whispercli.components.result

import it.andreag.whispercli.model.ParsedLine
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableColumn

class MinuteTableColumn : TableColumn<ParsedLine, String>() {
    init {
        text = "Position"
        isResizable = false
        setCellValueFactory {
            SimpleStringProperty(buildString {
                append(it.value.from.toString())
                append(" - ")
                append(it.value.to.toString())
            })
        }
        isSortable = false

        setCellFactory {
            return@setCellFactory TextTableCell()
        }
    }
}