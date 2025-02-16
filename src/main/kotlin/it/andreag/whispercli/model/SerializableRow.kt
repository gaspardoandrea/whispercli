package it.andreag.whispercli.model

import kotlinx.serialization.Serializable

@Serializable
data class SerializableRow(val from: String, val to: String, val text: String)
