package it.andreag.whispercli.components

import it.andreag.whispercli.model.AudioFile
import javafx.scene.control.Label

class PanelNotStarted: Label() {
    private var selectedFile: AudioFile? = null

    fun updateFromFile(audioFile: AudioFile) {
        this.selectedFile = audioFile
        audioFile.onMedia {
            val media = audioFile.getMedia()
            text = audioFile.file.name + " " + (media?.duration.toString()) + " " + (media?.tracks.toString())
//            MediaPlayerManager.getInstance().play(audioFile)
        }
    }
}