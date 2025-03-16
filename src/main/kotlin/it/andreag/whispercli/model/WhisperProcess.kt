package it.andreag.whispercli.model

import it.andreag.whispercli.WhisperApplication
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
            val lang = getLanguage()
            val inputFile = audioFile.filePath
            val outputPath = audioFile.getOutputDir(transcriptionModel).absolutePath

//            val args = arrayOf(
//                "chcp 850 ;",
//                Utils.getPythonBin(),
//                "-u",
//                "-m",
//                "whisper",
//                "--fp16",
//                "False",
//                "--language",
//                lang,
//                inputFile,
//                "--model",
//                transcriptionModel,
//                "--output_dir",
//                outputPath
//            )
//
//            println(args.joinToString(" "))
//
//            val pb = ProcessBuilder(*args)
            Utils.getInstance().setExecutionPolicy()
            val bin = "C:\\Program Files\\PowerShell\\7\\pwsh.exe"
            val file = Utils.getInstance().getPs1("whisper.ps1")
            val py = Utils.getInstance().getPs1("faster.py")
            val pb = ProcessBuilder(bin, "-File", file, lang, inputFile, transcriptionModel, outputPath, py)

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