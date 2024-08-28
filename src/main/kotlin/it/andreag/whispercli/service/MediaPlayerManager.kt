package it.andreag.whispercli.service

import it.andreag.whispercli.model.AudioFile
import it.andreag.whispercli.model.ParsedLine
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration

class MediaPlayerManager {
    private var playingMediaPlayer: MediaPlayer? = null

    fun getPlayerFor(media: Media): MediaPlayer {
        return MediaPlayer(media)
    }

    private fun setPlayingPlayer(player: MediaPlayer?) {
        playingMediaPlayer?.stop()
        playingMediaPlayer = player
    }

    fun play(audioFile: AudioFile) {
        val player = audioFile.getPlayer()
        setPlayingPlayer(player)
        player?.play()
    }

    fun play(audioLine: ParsedLine) {
        val player = audioLine.audioFile.getPlayer()
        setPlayingPlayer(player)
        if (player == null) {
            return
        }
        val from = audioLine.from.toSecondOfDay()
        if (player.status == MediaPlayer.Status.PLAYING) {
            player.stop()
        }
        player.seek(Duration(0.0))
        player.seek(Duration((from * 1000).toDouble()))
        player.play()
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