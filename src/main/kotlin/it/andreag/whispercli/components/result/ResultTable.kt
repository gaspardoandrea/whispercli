package it.andreag.whispercli.components.result

import it.andreag.whispercli.model.ParsedLine
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TableView

class ResultTable() : TableView<ParsedLine>() {
    private val textCol: TextTableColumn = TextTableColumn()
    private val minuteCol: MinuteTableColumn = MinuteTableColumn()
    val dataList: ObservableList<ParsedLine> = FXCollections.observableArrayList()
        get() = field

    init {
        isEditable = true
        minuteCol.prefWidth = 200.0
        textCol.prefWidthProperty().bind(widthProperty().subtract(30).subtract(minuteCol.width))
        columns.add(minuteCol)
        columns.add(textCol)

        items = dataList
    }
}