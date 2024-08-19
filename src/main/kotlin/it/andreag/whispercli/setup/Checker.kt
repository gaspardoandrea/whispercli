package it.andreag.whispercli.setup

import it.andreag.whispercli.events.ThreadDispatcher
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.scene.control.Label
import mu.KotlinLogging
import java.awt.Desktop
import java.net.URI

class Checker(private val label: Label, private val delayed: Boolean) :
    Task<Boolean>() {
    private val pythonInstallUrl = "https://www.python.org/downloads/windows/"
    private val logger = KotlinLogging.logger {}


    override fun call(): Boolean {
        logger.debug { "Check for correct install" }
        ThreadDispatcher.getInstance().addBlockingThread(this)
        if (delayed) {
            Thread.sleep(1500L)
        }
        val rv = checkInstall()

        return rv
    }

    private fun checkInstall(): Boolean {
        showCheckInfoLabel()
        Thread.sleep(250L)
        if (Utils.getInstance().checkPython()) {
            showPythonOk()
            Thread.sleep(200L)
        } else {
            showPythonNok()
            Thread.sleep(2000L)
            disposeLabel()
            return false
        }
        if (Utils.getInstance().checkWhisper()) {
            showWhisperOk()
            Thread.sleep(200L)
            disposeLabel()
        } else {
            showWhisperNok()
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

    private fun showWhisperNok() {
        showError("Whisper not installed properly")
        Thread.sleep(1000)
        showMessage("Running install process")
        Utils.getInstance().installWhisper()
    }

    private fun showWhisperOk() {
        showMessage("Whisper installed properly")
        Thread.sleep(1500)
    }

    private fun showPythonNok() {
        showError("Python not installed properly")
        Thread.sleep(1500L)
        showError("Install the last Python version for Windows!")
        Thread.sleep(2500L)
        openBrowser()
    }

    private fun openBrowser() {
        try {
            Desktop.getDesktop().browse(URI(pythonInstallUrl))
        } catch (e: Exception) {
            logger.error { e }
        }
    }

    private fun showPythonOk() {
        showMessage("Python installed properly")
    }

    private fun showCheckInfoLabel() {
        showMessage("Checking installation")
    }

    private fun showMessage(s: String) {
        Platform.runLater {
            label.text = s
            label.isVisible = true
        }
    }

    private fun showError(s: String) {
        Platform.runLater {
            label.text = s
            label.isVisible = true
        }
    }
}