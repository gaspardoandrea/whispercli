package it.andreag.whispercli.model

import it.andreag.whispercli.events.ThreadDispatcher
import it.andreag.whispercli.setup.Utils
import javafx.concurrent.Task

class WhisperProcess(private val audioFile: AudioFile): Task<Boolean>() {
    override fun call(): Boolean {
        ThreadDispatcher.getInstance().addNonBlockingThread(this)
        Utils.getInstance().startTranscribeProcess(audioFile)
        audioFile.updateStatus()

        return true
    }
}