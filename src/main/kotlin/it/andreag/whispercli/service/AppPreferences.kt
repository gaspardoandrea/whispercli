package it.andreag.whispercli.service

import javafx.geometry.Dimension2D
import java.io.File
import java.util.prefs.Preferences


class AppPreferences private constructor() {
    private var preferences: Preferences = Preferences.userNodeForPackage(AppPreferences::class.java)

    fun setFileChooserLocation(parentFile: File?) {
        this.preferences.put("FileChooserLocation", parentFile?.absolutePath)
    }

    fun getFileChooserLocation(): File? {
        val path = this.preferences.get("FileChooserLocation", null) ?: return null
        val file = File(path)
        if (file.isDirectory) {
            return file
        }
        return null
    }

    fun setWindowSize(width: Double, height: Double) {
        this.preferences.putDouble("windowSizeHeight", height)
        this.preferences.putDouble("windowSizeWidth", width)
    }

    fun getWindowSize(): Dimension2D? {
        if (preferences.getDouble("windowSizeHeight", -1.0) == -1.0) {
            return null
        }
        return Dimension2D(
            this.preferences.getDouble("windowSizeWidth", 600.0), this.preferences.getDouble("windowSizeHeight", 800.0)
        )
    }

    fun setSplitPositions(dividerPositions: DoubleArray?) {
        if (dividerPositions != null) {
            this.preferences.putDouble("dividerPosition", dividerPositions.first())
        }
    }

    fun getSplitPosition(): Double? {
        if (this.preferences.getDouble("dividerPosition", -1.0) == -1.0) {
            return null
        }
        return this.preferences.getDouble("dividerPosition", -1.0)
    }

    fun setTranscriptionModel(id: String?) {
        val tid = id?.replace("model", "")?.lowercase()
        this.preferences.put("TranscriptionModel", tid)
    }

    fun getTranscriptionModel(): String {
        return this.preferences.get("TranscriptionModel", "tiny")
    }

    fun setCheckOnStartup(bool: Boolean) {
        this.preferences.putBoolean("CheckOnStartup", bool)
    }

    fun checkOnStartup(): Boolean {
        return this.preferences.getBoolean("CheckOnStartup", true)
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