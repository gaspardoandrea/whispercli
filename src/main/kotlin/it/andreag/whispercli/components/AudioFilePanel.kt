package it.andreag.whispercli.components

import it.andreag.whispercli.components.insets.SmallInsets
import it.andreag.whispercli.model.AudioFile
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane

abstract class AudioFilePanel : BorderPane() {
    protected val br = "\r\n"
    private var titleLabel: Label = Label()

    init {
        top = titleLabel
        titleLabel.style = "-fx-font-weight: bold; -fx-font-size: 1.5em"
        setMargin(titleLabel, SmallInsets())
    }

    open fun updateFromFile(transcriptionModel: String, audioFile: AudioFile) {
        if (needSave()) {
            save()
        }
        titleLabel.text = audioFile.description + " " + audioFile.getFormattedPercent()
        updateContent(transcriptionModel, audioFile)
    }

    abstract fun needSave(): Boolean
    abstract fun save()

    abstract fun updateContent(transcriptionModel: String, audioFile: AudioFile)
}