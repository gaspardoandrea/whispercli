package it.andreag.whispercli.model

import it.andreag.whispercli.service.AppPreferences
import it.andreag.whispercli.service.ApplicationPersistence
import it.andreag.whispercli.service.MediaPlayerManager
import javafx.application.Platform
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import java.io.File
import java.security.MessageDigest
import java.util.regex.Matcher
import java.util.regex.Pattern


@Serializable
data class AudioFile(
    val filePath: String,
    var description: String = File(filePath).name
) :
    Observable() {

    @Transient
    private var currentThread: Thread? = null

    @Transient
    private var media: Media? = null

    @Transient
    private var player: MediaPlayer? = null

    @Transient
    private var currentStatus: AudioFileStatus? = null

    @Transient
    val file: File = File(filePath)

    @Transient
    var percent: Int? = null

    @Transient
    val sourceParsedLines: ArrayList<SourceParsedLine> = ArrayList()

    @Transient
    val editedLines: ArrayList<ParsedLine> = ArrayList()

    init {
        super.setObservableObject(this)
    }

    fun getIcon(): String {
        return getStatus().toIcon()
    }

    fun isNew(): Boolean {
        return getStatus().isNew()
    }

    fun isTranscribed(): Boolean {
        return getStatus().isTranscribed()
    }

    fun isTranscribing(): Boolean {
        return getStatus().isTranscribing()
    }

    fun getMedia(): Media? {
        if (media == null) {
            media = Media(file.toURI().toString())
        }
        return media
    }

    fun onMedia(runnable: Runnable) {
        val media = getMedia() ?: return
        if (player == null) {
            player = MediaPlayerManager.getInstance().getPlayerFor(media)
            player!!.onReady = runnable
        } else {
            runnable.run()
        }
    }

    fun getPlayer(): MediaPlayer? {
        return player
    }

    fun startWhisper() {
        percent = 0
        currentThread = Thread(WhisperProcess(this))
        currentThread!!.isDaemon = true
        currentThread!!.start()
        sourceParsedLines.clear()
        updateStatus()
    }

    fun getOutputDir(transcriptionModel: String): File {
        val rv = File(getOutputDir(), transcriptionModel)
        rv.mkdirs()
        return rv
    }

    private fun getOutputDir() = File(ApplicationPersistence.getInstance().getDataPath(), getNormalizedName())

    private fun getNormalizedName(): String {
        val md = MessageDigest.getInstance("MD5")
        val hashBytes = md.digest(file.absolutePath.toByteArray())
        val hexString = hashBytes.joinToString("-") { "%02x".format(it) }

        return buildString {
            append(file.name)
            append("-")
            append(hexString)
        }
    }

    fun deleteAllOutputFiles() {
        getOutputDir().deleteRecursively()
        updateStatus()
    }

    fun updateStatus() {
        val oldState = currentStatus
        val newValue = getStatus()
        propertyChangeSupport?.firePropertyChange("status", oldState, newValue)
    }

    private fun getStatus(): AudioFileStatus {
        val transcriptionModel = AppPreferences.getInstance().getTranscriptionModel()
        return if (currentThread != null && currentThread!!.isAlive) {
            AudioFileStatus.Transcribing
        } else if (hasFilesFor(transcriptionModel)) {
            AudioFileStatus.Transcribed
        } else {
            AudioFileStatus.New
        }
    }

    private fun hasFilesFor(transcriptionModel: String): Boolean {
        return File(getOutputDir(transcriptionModel), getTxtFileName()).exists()
    }

    private fun getTxtFileName(): String {
        return getFileNameWithExt("txt")
    }

    private fun getFileNameWithExt(ext: String, postFix: String = ""): String {
        return getFileNameWithout() + postFix + "." + ext
    }

    private fun getFileNameWithout(): String {
        return file.name.replace(Regex("\\.\\w+$"), "")
    }

    fun updatePercentFromString(line: String) {
        onMedia {
            val l = SourceParsedLine.fromConsole(line, this)
            if (!l.valid) {
                return@onMedia
            }
            val media = getMedia()
            val now = l.to
            val max = SourceParsedLine.timeFromMs(media?.duration.toString())
            val old = percent
            percent = ((now.toNanoOfDay().toDouble() / max.toNanoOfDay().toDouble()) * 100).toInt()
            sourceParsedLines.add(l)
            Platform.runLater {
                propertyChangeSupport?.firePropertyChange("text", old, percent)
            }
        }
    }

    fun renderLabel(): String {
        return description + getFormattedPercent()
    }

    fun getFormattedPercent(): String {
        return if (isTranscribing() && percent != null) " [$percent%]" else ""
    }

    fun renderTooltip(): String? {
        return file.name
    }

    fun getFormattedDuration(): String {
        val media = getMedia()
        val duration = media?.duration
        if (duration != null) {
            return String.Companion.format(
                "%02.0f:%02.0f:%02.0f",
                duration.toHours(),
                duration.toMinutes(),
                duration.toSeconds()
            )
        }
        return ""
    }

    fun loadParsedLines(transcriptionModel: String) {
        sourceParsedLines.clear()
        val indexFile = File(getOutputDir(transcriptionModel), getFileNameWithExt("json"))
        val editedFile = getEditedFile(transcriptionModel)
        if (!indexFile.exists()) {
            return
        }
        val json = indexFile.readText()
        val audio = Json.decodeFromString<AudioFileData>(json)
        audio.segments.forEach {
            sourceParsedLines.add(SourceParsedLine.fromAudioLine(it, this))
        }
        if (editedFile.exists()) {
            loadEditedFile(editedFile)
        } else {
            loadEditedLinesFromSource()
            saveEditedFile(editedFile)
        }
    }

    private fun getEditedFile(transcriptionModel: String): File {
        return File(getOutputDir(transcriptionModel), getFileNameWithExt("json", "-edit"))
    }

    private fun loadEditedLinesFromSource(): ArrayList<ParsedLine> {
        editedLines.clear()
        sourceParsedLines.forEach { editedLines.add(ParsedLine.fromSourceParsedLine(it)) }
        return editedLines
    }

    private fun saveEditedFile(
        file: File
    ) {
        val json = Json.encodeToString(toJson())
        file.writeText(json)
    }

    private fun loadEditedFile(file: File) {
        editedLines.clear()
        var json: ArrayList<SerializableRow>
        var jsonText: String = file.readText()

        json = Json.decodeFromString(jsonText)
        json.forEach { editedLines.add(ParsedLine.fromSerialized(it, this)) }
    }

    fun toJson(): ArrayList<SerializableRow> {
        var rv = ArrayList<SerializableRow>()
        editedLines.forEach {
            val o = SerializableRow(
                it.from.toString(),
                it.to.toString(),
                it.text
            )
            rv.add(o)
        }
        return rv
    }

    fun saveFromEditor(text: String) {
        var rows = ArrayList<SerializableRow>()
        val pattern: Pattern = Pattern.compile("\\d+:\\d+:\\d+\\.\\d+ \\d+:\\d+:\\d+\\.\\d+")
        val split = pattern.splitWithDelimiters(text, -1)
        var delimiter: String? = null
        split.forEach {
            if (it.trim() == "") {
                return@forEach
            }
            if (pattern.matcher(it).matches()) {
                delimiter = it
            } else {
                val fromTo = delimiter?.split(" ")
                rows.add(SerializableRow(
                    fromTo?.get(0) ?: "",
                    fromTo?.get(1) ?: "",
                    it.trim()))
            }
        }

        val editedFile = getEditedFile(AppPreferences.getInstance().getTranscriptionModel())
        val json = Json.encodeToString(rows)
        editedFile.writeText(json)
        loadEditedFile(editedFile)
    }
}
