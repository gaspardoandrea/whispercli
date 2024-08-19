package it.andreag.whispercli.model

import kotlinx.serialization.Serializable

@Serializable
data class AudioLine(
    val id: Int,
    val seek: Int,
    val start: Double,
    val end: Double,
    val text: String,
    val tokens: List<Int>,
    val temperature: Double,
    val avg_logprob: Double,
    val compression_ratio: Double,
    val no_speech_prob: Double
)