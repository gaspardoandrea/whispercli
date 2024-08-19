package it.andreag.whispercli.model

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer

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

    companion object {
        @Volatile
        private var instance: MediaPlayerManager? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: MediaPlayerManager().also { instance = it }
            }
    }
}