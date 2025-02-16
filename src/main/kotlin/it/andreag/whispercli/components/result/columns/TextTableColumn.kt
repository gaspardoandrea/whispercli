package it.andreag.whispercli.components.result.columns

import it.andreag.whispercli.model.TableAudioLine
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableColumn
import java.util.*

class TextTableColumn : TableColumn<TableAudioLine, String>() {
    init {
        val bundle: ResourceBundle? = ResourceBundle.getBundle("it.andreag.whispercli.bundle")
        text = bundle?.getString("text")
        isResizable = false
        setCellValueFactory {
            SimpleStringProperty(it.value.getValueToShow())
        }
        isSortable = false
    }
}