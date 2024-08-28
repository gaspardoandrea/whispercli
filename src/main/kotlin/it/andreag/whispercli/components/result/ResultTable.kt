package it.andreag.whispercli.components.result

import it.andreag.whispercli.components.result.columns.MinuteTableColumn
import it.andreag.whispercli.components.result.columns.TextTableColumn
import it.andreag.whispercli.model.TableAudioLine
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView

class ResultTable() : TableView<TableAudioLine>() {
    private val textCol: TextTableColumn = TextTableColumn()
    private val minuteCol: MinuteTableColumn = MinuteTableColumn()
    val dataList: ObservableList<TableAudioLine> = FXCollections.observableArrayList()
        get() = field

    init {
        isEditable = true
        minuteCol.prefWidth = 200.0
        textCol.prefWidthProperty().bind(widthProperty().subtract(30).subtract(minuteCol.width))
        columns.add(minuteCol)
        columns.add(textCol)

        textCol.setOnEditCommit { t: TableColumn.CellEditEvent<TableAudioLine?, String?> ->
            val item = t.getTableView().getItems().get(t.getTablePosition().getRow()) ?: return@setOnEditCommit
            item.updatedText = t.newValue.toString()
        }

        items = dataList
    }
}