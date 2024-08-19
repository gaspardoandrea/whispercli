package it.andreag.whispercli.setup

import it.andreag.whispercli.WhisperApplication
import it.andreag.whispercli.model.AppPreferences
import it.andreag.whispercli.model.AudioFile
import mu.KotlinLogging
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


class Utils {
    private val logger = KotlinLogging.logger {}

    fun checkWhisper(): Boolean {
        try {
            val pb = ProcessBuilder(getPythonBin(), "-m", "whisper")
            pb.redirectErrorStream(true)
            val process = pb.start()
            val builder = StringBuilder()
            var line: String?
            val reader =
                BufferedReader(InputStreamReader(process.inputStream))
            while ((reader.readLine().also { line = it }) != null) {
                builder.append(line)
                builder.append(System.lineSeparator())
            }
            val result = builder.toString()
            if (result.contains("No module named")) {
                throw Exception(result)
            }
            return true
        } catch (e: Exception) {
            logger.error { e }
            return false
        }
    }

    fun installWhisper(): Boolean {
        try {
            val bin = "C:\\Program Files\\PowerShell\\7\\pwsh.exe"
            val file = WhisperApplication::class.java.getResource("install.ps1")?.file.toString().trimStart('/')
            val pb = ProcessBuilder(bin, "-File", file)
            logger.error { "$bin -File $file" }
            pb.redirectErrorStream(true)
            val process = pb.start()
            val builder = StringBuilder()
            var line: String?
            val reader =
                BufferedReader(InputStreamReader(process.inputStream))
            while ((reader.readLine().also { line = it }) != null) {
                builder.append(line)
                builder.append(System.lineSeparator())
            }
            val result = builder.toString()
            if (result.contains("No module named")) {
                throw Exception(result)
            }
            return true
        } catch (e: Exception) {
            logger.error { e }
            return false
        }
    }

    fun checkPython(): Boolean {
        try {
            ProcessBuilder(getPythonBin(), "-V").start()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    private fun getPythonBin(): String {
        return File(System.getProperty("user.home"), "AppData\\Local\\Programs\\Python\\Launcher\\py.exe").absolutePath
    }

    fun startTranscribeProcess(audioFile: AudioFile) {
        try {
            val transcriptionModel = AppPreferences.getInstance().getTranscriptionModel()

            val pb = ProcessBuilder(
                getPythonBin(),
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
            pb.redirectErrorStream(true)
            val process = pb.start()
            val processStdOutput = process.inputStream
            processStdOutput.bufferedReader(Charsets.UTF_8).forEachLine {
                println(it)
                audioFile.updatePercentFromString(it)
            }
        } catch (e: Exception) {
            logger.error { e }
        }
    }

    private fun getLanguage() = "it"

    fun updateWhisper(): Boolean {
        try {
            val bin = "C:\\Program Files\\PowerShell\\7\\pwsh.exe"
            val file = WhisperApplication::class.java.getResource("update.ps1")?.file.toString().trimStart('/')
            val pb = ProcessBuilder(bin, "-File", file)
            logger.error { "$bin -File $file" }
            pb.redirectErrorStream(true)
            val process = pb.start()
            val builder = StringBuilder()
            var line: String?
            val reader =
                BufferedReader(InputStreamReader(process.inputStream))
            while ((reader.readLine().also { line = it }) != null) {
                builder.append(line)
                builder.append(System.lineSeparator())
            }
            val result = builder.toString()
            if (result.contains("No module named")) {
                throw Exception(result)
            }
            return true
        } catch (e: Exception) {
            logger.error { e }
            return false
        }
    }

    companion object {
        @Volatile
        private var instance: Utils? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: Utils().also { instance = it }
            }
    }
}