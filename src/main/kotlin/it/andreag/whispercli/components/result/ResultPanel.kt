package it.andreag.whispercli.components.result

import it.andreag.whispercli.components.AudioFilePanel
import it.andreag.whispercli.model.AudioFile

class ResultPanel : AudioFilePanel() {
    private val editor: Editor = Editor()

    init {
        center = editor
    }

    override fun updateContent(transcriptionModel: String, audioFile: AudioFile) {
        editor.updateEditorContent(audioFile, transcriptionModel, false)
    }

    override fun needSave(): Boolean {
        return editor.needSave()
    }

    override fun save() {
        return editor.save()
    }
}