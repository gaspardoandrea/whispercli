package it.andreag.whispercli.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport


@Serializable
open class Observable {
    @Transient
    protected var propertyChangeSupport: PropertyChangeSupport? = null

    fun setObservableObject(observable: Observable?) {
        propertyChangeSupport = PropertyChangeSupport(observable)
    }

    fun addPropertyChangeListener(listener: PropertyChangeListener?) {
        propertyChangeSupport!!.addPropertyChangeListener(listener)
    }
}