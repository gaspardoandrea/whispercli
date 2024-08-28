package it.andreag.whispercli.components.result

import it.andreag.whispercli.model.ParsedLine
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TableView

class ResultTable() : TableView<ParsedLine>() {
    private val lineText: TextTableColumn = TextTableColumn()
    val dataList: ObservableList<ParsedLine> = FXCollections.observableArrayList()
        get() = field

    init {
        isEditable = true
        lineText.prefWidthProperty().bind(widthProperty().subtract(30))
        columns.add(lineText)
        setItems(dataList)
    }
}