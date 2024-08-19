package it.andreag.whispercli.setup

import it.andreag.whispercli.events.ThreadDispatcher
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.scene.control.Label
import mu.KotlinLogging

class ComponentsUpdater(private val label: Label) :
    Task<Boolean>() {
    private val logger = KotlinLogging.logger {}

    override fun call(): Boolean {
        logger.debug { "Updating components" }
        ThreadDispatcher.getInstance().addBlockingThread(this)
        val rv = runUpdate()

        return rv
    }

    private fun runUpdate(): Boolean {
        showCheckInfoLabel()
        Thread.sleep(250L)
        if (Utils.getInstance().updateWhisper()) {
            showWhisperUpdate()
            Thread.sleep(200L)
            disposeLabel()
        } else {
            showWhisperNotUpdated()
            Thread.sleep(4000L)
            disposeLabel()
        }
        return true
    }

    private fun disposeLabel() {
        Platform.runLater {
            label.isVisible = false
        }
    }

    private fun showWhisperUpdate() {
        showMessage("Whisper updated")
        Thread.sleep(1000)
    }

    private fun showWhisperNotUpdated() {
        showMessage("Whisper already updated")
        Thread.sleep(1500)
    }

    private fun showCheckInfoLabel() {
        showMessage("Checking for updates")
    }

    private fun showMessage(s: String) {
        Platform.runLater {
            label.text = s
            label.isVisible = true
        }
    }
}