package it.andreag.whispercli.model

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.io.File


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
    val parsedLines: ArrayList<ParsedLine> = ArrayList()

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
        parsedLines.clear()
        updateStatus()
    }

    fun getOutputDir(transcriptionModel: String): File {
        val rv = File(getOutputDir(), transcriptionModel)
        rv.mkdirs()
        return rv
    }

    private fun getOutputDir() = File(ApplicationPersistence.getInstance().getDataPath(), getNormalizedName())

    private fun getNormalizedName(): String {
        return file.name
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

    private fun getFileNameWithExt(ext: String): String {
        return getFileNameWithout() + "." + ext
    }

    private fun getFileNameWithout(): String {
        return file.name.replace(Regex("\\.\\w+$"), "")
    }

    fun updatePercentFromString(line: String) {
        onMedia {
            val l = ParsedLine(line)
            if (!l.valid) {
                return@onMedia
            }
            val media = getMedia()
            val now = l.to
            val max = ParsedLine.timeFromMs(media?.duration.toString())
            val old = percent
            percent = ((now.toNanoOfDay().toDouble() / max.toNanoOfDay().toDouble()) * 100).toInt()
            parsedLines.add(l)
            propertyChangeSupport?.firePropertyChange("text", old, percent)
        }
    }

    fun renderLabel(): String {
        return description + if (isTranscribing() && percent != null) " [$percent%]" else ""
    }

    fun renderTooltip(): String? {
        return file.name
    }
}