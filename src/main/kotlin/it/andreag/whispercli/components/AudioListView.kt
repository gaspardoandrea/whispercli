package it.andreag.whispercli.components

import it.andreag.whispercli.model.AudioFile
import javafx.event.EventHandler
import javafx.scene.control.ListView
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode

class AudioListView : ListView<AudioFile>()