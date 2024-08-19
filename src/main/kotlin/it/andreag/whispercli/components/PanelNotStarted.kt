package it.andreag.whispercli.components

import it.andreag.whispercli.model.AudioFile
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow

class PanelNotStarted : BorderPane() {
    private val br = "\r\n"
    private var titleLabel: Label = Label()
    private var textFlow: TextFlow = TextFlow()

    init {
        top = titleLabel
        titleLabel.style = "-fx-font-weight: bold; -fx-font-size: 1.5em"
        center = textFlow
        setMargin(titleLabel, SmallInsets())
        setMargin(textFlow, BigInsets())
    }

    fun updateFromFile(audioFile: AudioFile) {
        titleLabel.text = audioFile.description
        audioFile.onMedia {
            textFlow.children.clear()

            val media = audioFile.getMedia()
            val text1 = Text(audioFile.file.name + br)
            text1.style = "-fx-font-weight: bold"

            val text2 = Text("Duration: ")
            val text3 = Text(audioFile.getFormattedDuration() + br)
            text3.style = "-fx-font-family: monospace"

            textFlow.children.addAll(text1, text2, text3)
            media?.tracks?.forEach {
                textFlow.children.add(Text(br))
                textFlow.children.add(Text("Track: "))
                val trackId = Text(it.trackID.toString())
                trackId.style = "-fx-font-family: monospace"
                textFlow.children.add(trackId)
                textFlow.children.add(Text(" "))
                val trackName = Text(it.name)
                trackName.style = "-fx-font-weight: bold"
                textFlow.children.add(trackName)
                textFlow.children.add(Text(br))
                it.metadata.forEach { it2 ->
                    textFlow.children.add(Text(it2.key))
                    textFlow.children.add(Text(": "))
                    val value = Text(it2.value.toString())
                    value.style = "-fx-font-family: monospace"
                    textFlow.children.add(value)
                    textFlow.children.add(Text(br))
                }
                textFlow.children.add(Text(br))
            }
        }
    }
}