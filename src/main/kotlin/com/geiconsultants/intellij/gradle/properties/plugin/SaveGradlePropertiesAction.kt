package com.geiconsultants.intellij.gradle.properties.plugin

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileDocumentManager

class SaveGradlePropertiesAction(
    private val panel: GradlePropertiesPanel
) : AnAction(
    "Save",
    "Preview and save gradle.properties",
    AllIcons.Actions.MenuSaveall
) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        // Ensure editors are flushed
        FileDocumentManager.getInstance().saveAllDocuments()

        val result =
            GradlePropertiesSaveService.buildUpdatedContent(
                project,
                panel.getEntries()
            ) ?: return

        val (file, updatedContent) = result

        val accepted =
            GradlePropertiesDiffPreview.show(project, file, updatedContent)

        if (accepted) {
            GradlePropertiesSaveService.apply(
                project,
                file,
                updatedContent
            )
        }
    }
}
