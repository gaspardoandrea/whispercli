package it.andreag.whispercli.service

import it.andreag.whispercli.model.AudioFile
import it.andreag.whispercli.model.ParsedLine
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration
import java.util.*

class MediaPlayerManager {
    private var playingMediaPlayer: MediaPlayer? = null
    var stopTimer: Timer = Timer()
    var timerTask: TimerTask? = null

    fun getPlayerFor(media: Media): MediaPlayer {
        return MediaPlayer(media)
    }

    private fun setPlayingPlayer(player: MediaPlayer?) {
        playingMediaPlayer?.stop()
        playingMediaPlayer = player
    }

    fun play(audioFile: AudioFile) {
        audioFile.onMedia {
            val player = audioFile.getPlayer()
            if (player != null) {
                setPlayingPlayer(player)
                player.seek(Duration(0.toDouble()))
                player.startTime = Duration((0).toDouble())
                player.play()
            }
        }
    }

    fun play(parsedLine: ParsedLine) {
        if (timerTask !== null) {
            timerTask!!.cancel()
        }
        stopTimer.purge()

        val player = parsedLine.audioFile.getPlayer()
        setPlayingPlayer(player)
        if (player == null) {
            return
        }
        if (player.status == MediaPlayer.Status.PLAYING) {
            player.stop()
        }
        player.seek(parsedLine.getFromDuration())
        player.startTime = parsedLine.getFromDuration()
        player.play()

        timerTask = object : TimerTask() {
            override fun run() {
                if (player.status == MediaPlayer.Status.PLAYING) {
                    player.stop()
                }
            }
        }
        stopTimer.schedule(timerTask, parsedLine.getLineDuration())
    }

    fun stop() {
        playingMediaPlayer?.stop()
    }

    companion object {
        @Volatile
        private var instance: MediaPlayerManager? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: MediaPlayerManager().also { instance = it }
            }
    }
}