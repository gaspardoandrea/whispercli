package it.andreag.whispercli.events

import java.util.*


interface ThreadEventListener : EventListener {
    fun onTaskListChanged()
}