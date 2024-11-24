package it.andreag.whispercli

import it.andreag.whispercli.components.AudioListView
import it.andreag.whispercli.components.result.ResultPanel
import it.andreag.whispercli.components.stack.PanelNotStarted
import it.andreag.whispercli.components.stack.PanelStarted
import it.andreag.whispercli.events.ThreadDispatcher
import it.andreag.whispercli.events.ThreadEventListener
import it.andreag.whispercli.model.AudioFile
import it.andreag.whispercli.service.AppPreferences
import it.andreag.whispercli.service.ApplicationPersistence
import it.andreag.whispercli.service.MediaPlayerManager
import it.andreag.whispercli.setup.Checker
import it.andreag.whispercli.setup.ComponentsUpdater
import javafx.application.Platform
import javafx.collections.ListChangeListener
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import mu.KotlinLogging
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.io.File
import java.net.URL
import java.util.*


class MainController : Initializable, ListChangeListener<AudioFile>, PropertyChangeListener, ThreadEventListener {
    lateinit var stopAudioFileMenuItem: Button
    lateinit var stopAudioFileItem: MenuItem
    lateinit var selectedModelLabel: Label
    lateinit var toggleCheckOnStartup: CheckMenuItem
    lateinit var stackPane: StackPane
    private val logger = KotlinLogging.logger {}
    lateinit var playAudioFileCtx: MenuItem
    lateinit var playAudioFileMenuItem: Button

    lateinit var playAudioFileItem: MenuItem
    lateinit var currentFileInfoStarted: PanelStarted
    lateinit var currentFileResult: ResultPanel
    lateinit var currentFileInfo: PanelNotStarted
    lateinit var modelTiny: RadioMenuItem
    lateinit var modelBase: RadioMenuItem
    lateinit var modelSmall: RadioMenuItem
    lateinit var modelMedium: RadioMenuItem
    lateinit var modelLarge: RadioMenuItem
    lateinit var modelTurbo: RadioMenuItem
    lateinit var panelMoreThanOne: ScrollPane

    /** Menu items */
    lateinit var updateWhisperSetupMenuItem: MenuItem
    lateinit var checkWhisperSetupMenuItem: MenuItem
    lateinit var infoLabel: Label
    lateinit var splitPane: SplitPane
    lateinit var resetFilesItemCtx: MenuItem
    lateinit var transcribeFilesItemCtx: MenuItem
    lateinit var removeItemCtx: MenuItem
    lateinit var setDescriptionItemCtx: MenuItem
    lateinit var contextMenu: ContextMenu
    lateinit var setDescriptionButton: Button
    lateinit var setDescriptionItem: MenuItem
    lateinit var resetFilesItem: MenuItem
    lateinit var resetFilesButton: Button
    lateinit var transcribeFilesButton: Button
    lateinit var transcribeFilesItem: MenuItem
    lateinit var removeItemButton: Button
    lateinit var removeItem: MenuItem
    lateinit var listView: AudioListView
    lateinit var mainPane: BorderPane

    val bundle: ResourceBundle? = ResourceBundle.getBundle("it.andreag.whispercli.bundle")

    override fun initialize(url: URL?, resource: ResourceBundle?) {
        listView.setCellFactory { AudioListCell() }
        ApplicationPersistence.getInstance().loadStatus(listView)
        listView.items.forEach { it.addPropertyChangeListener(this) }
        listView.selectionModel.selectionMode = SelectionMode.MULTIPLE
        ThreadDispatcher.getInstance().addListener(this)

        listView.selectionModel.selectedItems.addListener(this)
        toggleCheckOnStartup.isSelected = AppPreferences.getInstance().checkOnStartup()
        val splitPosition = AppPreferences.getInstance().getSplitPosition()
        if (splitPosition != null) {
            splitPane.setDividerPosition(0, splitPosition)
        }
        SplitPane.setResizableWithParent(listView, false)
        if (AppPreferences.getInstance().checkOnStartup()) {
            checkWhisperSetup(true)
        }
        updateTranscriptionModel()
        selectFirstItem()
    }

    private fun selectFirstItem() {
        if (listView.items.isEmpty()) return
        listView.selectionModel.select(0)
    }

    private fun updateTranscriptionModel() {
        val model = AppPreferences.getInstance().getTranscriptionModel()
        when (model) {
            "tiny" -> modelTiny
            "base" -> modelBase
            "small" -> modelSmall
            "medium" -> modelMedium
            "large" -> modelLarge
            "turbo" -> modelTurbo
            else -> modelTiny
        }.isSelected = true
    }

    override fun onChanged(audioFileChange: ListChangeListener.Change<out AudioFile>?) {
        refreshEnabledActions()
    }

    private fun saveStatus() {
        ApplicationPersistence.getInstance().saveStatus()
        AppPreferences.getInstance().setWindowSize(mainPane.scene.width, mainPane.scene.height)
        AppPreferences.getInstance().setSplitPositions(splitPane.dividerPositions)
    }

    @FXML
    fun exitApp() {
        saveStatus()
        Platform.exit()
    }

    @FXML
    fun addAudioFile() {
        val fileChooser = FileChooser()
        fileChooser.title = "Add audio files"
        val f: File? = AppPreferences.getInstance().getFileChooserLocation()
        if (f != null) {
            fileChooser.initialDirectory = f
        }
        val files = fileChooser.showOpenMultipleDialog(mainPane.scene.window) ?: return

        files.forEach {
            addAudioFileToList(it)
            AppPreferences.getInstance().setFileChooserLocation(it.parentFile)
        }
        refresh()
    }

    private fun addAudioFileToList(file: File?): Boolean {
        if (file == null) {
            return false
        }
        val audioFile = AudioFile(file.absolutePath)
        audioFile.addPropertyChangeListener(this)

        if (listView.items.contains(audioFile)) {
            return true
        }

        listView.items.add(audioFile)
        return false
    }

    private fun refreshEnabledActions() {
        logger.debug { "refreshEnabledActions" }

        removeItem.isDisable = isSelectionEmpty()
        transcribeFilesItem.isDisable = !selectionContainsNew() || ThreadDispatcher.getInstance().hasBlockingTasks()
        resetFilesItem.isDisable = !selectionContainsTranscribed()
        setDescriptionButton.isDisable = !isSelectionOne()
        playAudioFileItem.isDisable = !isSelectionOne()

        updateWhisperSetupMenuItem.isDisable = ThreadDispatcher.getInstance().hasTasks()
        checkWhisperSetupMenuItem.isDisable = ThreadDispatcher.getInstance().hasTasks()

        removeItemButton.isDisable = removeItem.isDisable
        resetFilesButton.isDisable = resetFilesItem.isDisable
        setDescriptionItem.isDisable = setDescriptionButton.isDisable
        transcribeFilesButton.isDisable = transcribeFilesItem.isDisable
        removeItemCtx.isDisable = removeItem.isDisable
        resetFilesItemCtx.isDisable = resetFilesItem.isDisable
        setDescriptionItemCtx.isDisable = setDescriptionButton.isDisable
        transcribeFilesItemCtx.isDisable = transcribeFilesItem.isDisable
        playAudioFileMenuItem.isDisable = playAudioFileItem.isDisable
        playAudioFileCtx.isDisable = playAudioFileItem.isDisable

        currentFileResult.isVisible = false
        currentFileInfoStarted.isVisible = false
        currentFileInfo.isVisible = false
        panelMoreThanOne.isVisible = false

        selectedModelLabel.text = bundle?.getString(AppPreferences.getInstance().getTranscriptionModel())

        updatePanels()
    }

    private fun updatePanels() {
        val model = AppPreferences.getInstance().getTranscriptionModel()
        if (isSelectionEmpty() || isSelectionMoreThanOne()) {
            panelMoreThanOne.isVisible = true
        } else if (isSelectedNew()) {
            currentFileInfo.updateFromFile(model, getSelectedFile())
            currentFileInfo.isVisible = true
        } else if (isSelectedDone()) {
            currentFileResult.updateFromFile(model, getSelectedFile())
            currentFileResult.isVisible = true
        } else if (isSelectedTranscribing()) {
            Platform.runLater {
                currentFileInfoStarted.updateFromFile(model, getSelectedFile())
                currentFileInfoStarted.isVisible = true
            }
        }
    }

    private fun getSelectedFile(): AudioFile {
        return listView.selectionModel.selectedItems.first()
    }

    private fun selectionContainsTranscribed(): Boolean {
        var rv = false
        listView.selectionModel.selectedItems.forEach { if (it.isTranscribed()) rv = true }
        return rv
    }

    private fun selectionContainsNew(): Boolean {
        var rv = false
        listView.selectionModel.selectedItems.forEach { if (it.isNew()) rv = true }
        return rv
    }

    private fun isSelectionEmpty(): Boolean {
        return listView.selectionModel.isEmpty
    }

    private fun isSelectedNew(): Boolean {
        return listView.selectionModel.selectedItems.first().isNew()
    }

    private fun isSelectedTranscribing(): Boolean {
        return listView.selectionModel.selectedItems.first().isTranscribing()
    }

    private fun isSelectedDone(): Boolean {
        return listView.selectionModel.selectedItems.first().isTranscribed()
    }

    private fun isSelectionMoreThanOne(): Boolean {
        return listView.selectionModel.selectedItems.size > 1
    }

    private fun isSelectionOne(): Boolean {
        return listView.selectionModel.selectedItems.size == 1
    }

    fun removeFiles() {
        val selection = getSelectionAsString()
        val alert = Alert(
            AlertType.CONFIRMATION,
            "Remove $selection ?", ButtonType.YES, ButtonType.NO
        )
        alert.showAndWait()
        if (alert.result != ButtonType.YES) {
            return
        }

        val items = listView.selectionModel.selectedItems.toTypedArray()
        listView.selectionModel.clearSelection()
        listView.items.removeAll(items.toSet())
        refresh()
    }

    fun transcribeFiles() {
        val items = listView.selectionModel.selectedItems.toTypedArray()
        items.forEach { it.startWhisper() }
        refresh()
    }

    fun resetFiles() {
        val selection = getSelectionAsString()
        val alert = Alert(
            AlertType.CONFIRMATION,
            "Reset $selection ?", ButtonType.YES, ButtonType.NO
        )
        alert.showAndWait()

        if (alert.result != ButtonType.YES) {
            return
        }
        val items = listView.selectionModel.selectedItems.toTypedArray()
        items.forEach { it.deleteAllOutputFiles() }
        refresh()
    }

    private fun refresh() {
        listView.refresh()
        refreshEnabledActions()
    }

    private fun getSelectionAsString(): String {
        val rv = LinkedList<String>()
        val items = listView.selectionModel.selectedItems.toTypedArray()
        items.forEach { rv.add(it.description) }
        return rv.joinToString(", ")
    }

    fun updateDescription() {
        val inputDialog = TextInputDialog(

        )
        inputDialog.title = "Update audio file description"
        inputDialog.contentText = "Audio file description"
        inputDialog.showAndWait()

        if (inputDialog.result == null) {
            return
        }
        val items = listView.selectionModel.selectedItems.toTypedArray()
        items.forEach { it.description = inputDialog.result }
        refresh()
    }

    fun checkWhisperSetup() {
        checkWhisperSetup(false)
    }

    private fun checkWhisperSetup(delayed: Boolean) {
        val th = Thread(Checker(infoLabel, delayed))
        th.isDaemon = true
        th.start()
    }

    fun updateWhisperSetup() {
        val th = Thread(ComponentsUpdater(infoLabel))
        th.isDaemon = true
        th.start()
    }

    fun toggleCheckOnStartup() {
        AppPreferences.getInstance().setCheckOnStartup(toggleCheckOnStartup.isSelected)
    }

    fun showAboutPopup() {
        logger.debug { "showAboutPopup" }
        val newWindow = Stage()
        newWindow.title = "About"
        val loader = FXMLLoader(javaClass.getResource("aboutWindow.fxml"))
        loader.resources = bundle
        newWindow.scene = Scene(loader.load())
        loader.getController<AboutWindow>().stage = newWindow
        newWindow.isAlwaysOnTop = true

        val css = this.javaClass.getResource("stylesheet.css")?.toExternalForm()
        newWindow.scene.stylesheets.add(css)

        newWindow.show()
    }

    fun setStage(stage: Stage) {
        stage.setOnCloseRequest { saveStatus() }
    }

    fun modelSelected(actionEvent: ActionEvent) {
        if (actionEvent.source !is RadioMenuItem) {
            return
        }
        AppPreferences.getInstance().setTranscriptionModel((actionEvent.source as RadioMenuItem).id)
        refresh()
    }

    fun playAudioFile() {
        MediaPlayerManager.getInstance().play(getSelectedFile())
    }

    fun stopAudioFile() {
        MediaPlayerManager.getInstance().stop()
    }

    override fun propertyChange(evt: PropertyChangeEvent?) {
        refresh()
    }

    override fun onTaskListChanged() {
        refreshEnabledActions()
    }

    fun handleDragOver(event: DragEvent) {
        if (event.gestureSource != this && event.dragboard.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY, TransferMode.MOVE)
        }
        event.consume();
    }

    fun handleDragDropped(event: DragEvent) {
        if (event.gestureSource != this && event.dragboard.hasFiles()) {
            event.dragboard.files.forEach {
                addAudioFileToList(it)
            }
        }
        event.consume();
    }
}