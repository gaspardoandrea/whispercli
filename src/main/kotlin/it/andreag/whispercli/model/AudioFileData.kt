package it.andreag.whispercli.model

import kotlinx.serialization.Serializable

@Serializable
data class AudioFileData(
//    val text: String,
    val startTime: Double,
    val endTime: Double,
    val language: String,
    val segments: List<AudioLine>
)