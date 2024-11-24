package it.andreag.whispercli.model

import it.andreag.whispercli.events.ThreadDispatcher
import it.andreag.whispercli.service.AppPreferences
import it.andreag.whispercli.setup.Utils
import javafx.concurrent.Task
import mu.KotlinLogging

class WhisperProcess(private val audioFile: AudioFile) : Task<Boolean>() {
    private val logger = KotlinLogging.logger {}

    override fun call(): Boolean {
        ThreadDispatcher.getInstance().addNonBlockingThread(this)
        startTranscribeProcess(audioFile)
        audioFile.updateStatus()

        return true
    }

    fun startTranscribeProcess(audioFile: AudioFile) {
        try {
            val transcriptionModel = AppPreferences.getInstance().getTranscriptionModel()

            val args = arrayOf(
                Utils.getPythonBin(),
                "-u",
                "-m",
                "whisper",
                "--fp16",
                "False",
                "--language",
                getLanguage(),
                audioFile.filePath,
                "--model",
                transcriptionModel,
                "--output_dir",
                audioFile.getOutputDir(transcriptionModel).absolutePath
            )

            println(args.joinToString(" "))

            val pb = ProcessBuilder(*args)
            pb.redirectErrorStream(true)
            val process = pb.start()
            val processStdOutput = process.inputStream
            processStdOutput.reader(Charsets.UTF_8).forEachLine {
                println(it)
                audioFile.updatePercentFromString(it)
            }
        } catch (e: Exception) {
            logger.error { e }
        }
    }

    private fun getLanguage() = "it"
}