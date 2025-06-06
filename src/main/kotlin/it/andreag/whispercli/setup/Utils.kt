package it.andreag.whispercli.setup

import it.andreag.whispercli.WhisperApplication
import mu.KotlinLogging
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.ResourceBundle


class Utils {
    private val logger = KotlinLogging.logger {}

    fun checkWhisper(): Boolean {
        try {
            val pb = ProcessBuilder(getPythonBin(), "-c", "import whisperx")
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

    fun getPs1(name: String): String {
        var rv = WhisperApplication::class.java.getResource(name)?.file.toString().trimStart('/')
        println(rv)
        val bundle: ResourceBundle? = ResourceBundle.getBundle("it.andreag.whispercli.bundle")
        val version = bundle?.getString("softwareVersion")
        if (rv.contains("!")) {
            rv = rv.replace("WhisperCli-$version.jar!/it/andreag/whispercli/", "resources/")
            println(rv)
            rv = rv.replace("file:/", "")
            println(rv)
            rv = rv.replace("/", "\\")
            println(rv)
        }
        return rv
    }

    fun setExecutionPolicy(): Boolean {
        try {
            val bin = getPowerShellExecutable()
            val args = "Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy Bypass"
            val pb = ProcessBuilder(bin, "-Command", args)
            pb.start()
            return true
        } catch (e: Exception) {
            logger.error { e }
            return false
        }
    }

    fun installWhisper(): Boolean {
        try {
            setExecutionPolicy()
            val bin = getPowerShellExecutable()
            val file = getPs1("install.ps1")
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
        } catch (_: Exception) {
            return false
        }
    }

    fun checkPowerShell(): Boolean {
        try {
            ProcessBuilder(getPowerShellExecutable(), "-help").start()
            return true
        } catch (_: Exception) {
            return false
        }
    }

    fun updateWhisper(): Boolean {
        try {
            setExecutionPolicy()
            val bin = getPowerShellExecutable()
            val file = getPs1("update.ps1")
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

    private fun getPowerShellExecutable(): String = "C:\\Program Files\\PowerShell\\7\\pwsh.exe"

    companion object {
        @Volatile
        private var instance: Utils? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: Utils().also { instance = it }
            }

        fun getPythonBin(): String {
            return File(
                System.getProperty("user.home"),
                "AppData\\Local\\Programs\\Python\\Launcher\\py.exe"
            ).absolutePath
        }

    }
}