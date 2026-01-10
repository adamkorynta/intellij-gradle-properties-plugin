package com.geiconsultants.intellij.gradle.properties.plugin

import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffManager
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.diff.util.DiffUserDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.JComponent

object GradlePropertiesDiffPreview {

    fun show(
        project: Project,
        file: VirtualFile,
        newContent: String
    ): Boolean {

        val contentFactory = DiffContentFactory.getInstance()

        val originalContent =
            contentFactory.create(project, file)

        val updatedContent =
            contentFactory.create(project, newContent)

        val request = SimpleDiffRequest(
            "Preview gradle.properties changes",
            originalContent,
            updatedContent,
            "Before Save",
            "After Save"
        )
        request.putUserData<Boolean>(DiffUserDataKeys.FORCE_READ_ONLY, true)
        val dialog = object : DialogWrapper(project) {
            init {
                title = "Review Changes"
                setOKButtonText("Apply")
                init()
            }

            override fun createCenterPanel(): JComponent {
                val panel = DiffManager.getInstance().createRequestPanel(project, disposable, null)
                panel.setRequest(request)
                return panel.component
            }

            override fun getDimensionServiceKey(): String = "GradlePropertiesDiffPreview"
        }

        return dialog.showAndGet()
    }
}
