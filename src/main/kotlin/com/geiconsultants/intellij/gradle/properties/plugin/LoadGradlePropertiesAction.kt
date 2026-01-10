package com.geiconsultants.intellij.gradle.properties.plugin

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileDocumentManager

class LoadGradlePropertiesAction(
    private val panel: GradlePropertiesPanel
) : AnAction(
    "Load / Refresh",
    "Force reload of gradle.properties files",
    AllIcons.Actions.Refresh
) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        FileDocumentManager.getInstance().saveAllDocuments()
        GradlePropertiesStateService.getInstance(project)
            .refresh()
    }
}