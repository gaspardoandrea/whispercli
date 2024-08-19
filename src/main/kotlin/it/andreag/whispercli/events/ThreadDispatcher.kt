package it.andreag.whispercli.events

import javafx.application.Platform
import javafx.concurrent.Task

class ThreadDispatcher {
    private val listeners: MutableSet<ThreadEventListener> = HashSet()
    private val blockingThread: MutableList<Task<Boolean>> = ArrayList()
    private val nonBlockingThread: MutableList<Task<Boolean>> = ArrayList()

    fun addBlockingThread(task: Task<Boolean>) {
        addLater(blockingThread, task)
    }

    fun addNonBlockingThread(task: Task<Boolean>) {
        addLater(nonBlockingThread, task)
    }

    private fun addLater(list: MutableList<Task<Boolean>>, task: Task<Boolean>) {
        Platform.runLater {
            list.add(task)
            val a = {
                list.remove(task)
                notifyEvent()
            }
            task.setOnSucceeded { a() }
            task.setOnFailed { a() }
            task.setOnCancelled { a() }

            notifyEvent()
        }
    }

    companion object {
        @Volatile
        private var instance: ThreadDispatcher? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: ThreadDispatcher().also { instance = it }
            }
    }


    fun addListener(eventListener: ThreadEventListener) {
        listeners.add(eventListener)
    }

    @Suppress("unused")
    fun removeListener(eventListener: ThreadEventListener) {
        listeners.remove(eventListener)
    }

    private fun notifyEvent() {
        listeners.forEach {
            it.onTaskListChanged()
        }
    }

    fun hasBlockingTasks(): Boolean {
        return blockingThread.isNotEmpty()
    }

    fun hasTasks(): Boolean {
        return !(blockingThread.isEmpty() && nonBlockingThread.isEmpty())
    }
}