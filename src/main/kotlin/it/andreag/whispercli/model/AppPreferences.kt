package it.andreag.whispercli.model

import javafx.geometry.Dimension2D
import java.io.File
import java.util.prefs.Preferences


class AppPreferences private constructor() {
    var prefs: Preferences = Preferences.userNodeForPackage(AppPreferences::class.java)


    fun setFileChooserLocation(parentFile: File?) {
        this.prefs.put("FileChooserLocation", parentFile?.absolutePath)
    }

    fun getFileChooserLocation(): File? {
        val path = this.prefs.get("FileChooserLocation", null) ?: return null
        val file = File(path)
        if (file.isDirectory) {
            return file
        }
        return null
    }

    fun setWindowSize(width: Double, height: Double) {
        this.prefs.putDouble("windowSizeHeight", height)
        this.prefs.putDouble("windowSizeWidth", width)
    }

    fun getWindowSize(): Dimension2D? {
        if (prefs.getDouble("windowSizeHeight", -1.0) == -1.0) {
            return null
        }
        return Dimension2D(
            this.prefs.getDouble("windowSizeWidth", 600.0), this.prefs.getDouble("windowSizeHeight", 800.0)
        )
    }

    fun setSplitPositions(dividerPositions: DoubleArray?) {
        if (dividerPositions != null) {
            this.prefs.putDouble("dividerPosition", dividerPositions.first())
        }
    }

    fun getSplitPosition(): Double? {
        if (this.prefs.getDouble("dividerPosition", -1.0) == -1.0) {
            return null
        }
        return this.prefs.getDouble("dividerPosition", -1.0)
    }

    fun setTranscriptionModel(id: String?) {
        val tid = id?.replace("model", "")?.lowercase()
        this.prefs.put("TranscriptionModel", tid)
    }

    fun getTranscriptionModel(): String {
        return this.prefs.get("TranscriptionModel", "tiny")
    }

    companion object {
        @Volatile
        private var instance: AppPreferences? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: AppPreferences().also { instance = it }
            }
    }
}