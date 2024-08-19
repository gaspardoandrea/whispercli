package it.andreag.whispercli

import it.andreag.whispercli.model.AudioFile
import javafx.scene.control.ListCell
import javafx.scene.control.Tooltip
import org.kordamp.ikonli.javafx.FontIcon

class AudioListCell : ListCell<AudioFile>() {
    public override fun updateItem(item: AudioFile?, isEmpty: Boolean) {
        super.updateItem(item, isEmpty)
        if (isEmpty) {
            text = null
            graphic = null
        }
        if (item != null) {
            text = item.renderLabel()
            tooltip = Tooltip(item.renderTooltip())

            val icon = FontIcon(item.getIcon() + ":24")
            graphic = icon
        }
    }
}