package it.andreag.whispercli.components

import it.andreag.whispercli.model.ParsedLine
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView

class ResultTable() : TableView<ParsedLine>() {
    private val lineText: TableColumn<ParsedLine, String> = TableColumn()
    val dataList: ObservableList<ParsedLine> = FXCollections.observableArrayList()
        get() = field

    init {
        isEditable = true
        lineText.prefWidth = 600.0
        lineText.text = "Text"
        lineText.setCellValueFactory {
            SimpleStringProperty(it.value.text)
        }
        columns.add(lineText)
        setItems(dataList)
    }
}