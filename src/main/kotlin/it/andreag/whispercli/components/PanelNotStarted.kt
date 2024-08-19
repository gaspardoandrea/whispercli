package it.andreag.whispercli.components

import it.andreag.whispercli.model.AudioFile
import javafx.scene.text.Text
import javafx.scene.text.TextFlow

class PanelNotStarted : AudioFilePanel() {
    private var textFlow: TextFlow = TextFlow()

    init {
        center = textFlow
        setMargin(textFlow, BigInsets())
    }

    override fun updateContent(transcriptionModel: String, audioFile: AudioFile) {
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