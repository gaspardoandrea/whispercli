package it.andreag.whispercli

import it.andreag.whispercli.model.AppPreferences
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage

//import mu.KotlinLogging


class WhisperApplication : Application() {
    lateinit var stage: Stage

    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(WhisperApplication::class.java.getResource("main-view.fxml"))
        val size = AppPreferences.getInstance().getWindowSize()
        val scene = Scene(fxmlLoader.load(), size?.width ?: 800.0, size?.height ?: 600.0)
        val css = this.javaClass.getResource("stylesheet.css")?.toExternalForm()

        scene.stylesheets.add(css)
        fxmlLoader.getController<MainController>().setStage(stage)
        this.stage = stage
        stage.icons.add(Image(WhisperApplication::class.java.getResourceAsStream("icon.png")))
        stage.title = "Whisper Cli"
        stage.scene = scene
        stage.show()
    }
}