package it.andreag.whispercli.components.result.columns

import it.andreag.whispercli.components.result.TextTableCell
import it.andreag.whispercli.model.TableAudioLine
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableColumn

class MinuteTableColumn : TableColumn<TableAudioLine, String>() {
    init {
        text = "Position"
        isResizable = false
        setCellValueFactory {
            SimpleStringProperty(buildString {
                append(it.value.audioLine.from.toString())
                append(" - ")
                append(it.value.audioLine.to.toString())
            })
        }
        isSortable = false

        setCellFactory {
            return@setCellFactory TextTableCell()
        }
    }
}