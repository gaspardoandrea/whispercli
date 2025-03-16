package it.andreag.whispercli.model

import kotlinx.serialization.Serializable

@Serializable
data class AudioFileData(
//    val text: String,
    val language: String,
    val segments: List<AudioLine>
)