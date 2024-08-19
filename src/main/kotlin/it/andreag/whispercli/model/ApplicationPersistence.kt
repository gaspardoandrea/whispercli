package it.andreag.whispercli.model

import javafx.collections.ListChangeListener
import javafx.scene.control.ListView
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


class ApplicationPersistence(): ListChangeListener<AudioFile> {
    private var saving: Boolean = true
    private var listView: ListView<AudioFile>? = null

    init {
        val dataPath = getDataPath()
        if (!dataPath.isDirectory) {
            dataPath.mkdirs()
        }
    }

    fun getDataPath(): File {
        val path = System.getProperty("user.home")
        return File("$path\\.whisperCli")
    }

    override fun onChanged(fileChange: ListChangeListener.Change<out AudioFile>?) {
        this.saveStatus()
    }

    private fun getIndexFile(): File {
        val dataPath = getDataPath()
        return File(dataPath, "index.json")
    }

    fun loadStatus(lv: ListView<AudioFile>) {
        listView = lv
        listView!!.items.addListener(this)
        val indexFile = getIndexFile()
        if (!indexFile.exists()) {
            return
        }
        val json = indexFile.readText()
        val files = Json.decodeFromString<List<AudioFile>>(json)
        disableSaving()
        listView!!.items.addAll(files)
        enableSaving()
    }

    private fun enableSaving() {
        saving = true
    }

    private fun disableSaving() {
        saving = false
    }

    fun saveStatus() {
        if (!saving) {
            return
        }
        if (listView == null) {
            return
        }
        val indexFile = getIndexFile()
        val audioFiles = listView!!.items.toTypedArray()
        val json = Json.encodeToString(value = audioFiles)
        indexFile.writeText(json)
    }

    companion object {
        @Volatile
        private var instance: ApplicationPersistence? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: ApplicationPersistence().also { instance = it }
            }
    }
}