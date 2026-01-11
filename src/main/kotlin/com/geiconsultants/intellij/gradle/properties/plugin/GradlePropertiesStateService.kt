package com.geiconsultants.intellij.gradle.properties.plugin

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Service(Service.Level.PROJECT)
class GradlePropertiesStateService(private val project: Project) {
    private val _stateUpdateCounter = MutableStateFlow(0)
    val stateUpdateEvents = _stateUpdateCounter.asStateFlow()

    @Volatile
    private var templates: List<VirtualFile> = emptyList()

    @Volatile
    private var gradleProperties: VirtualFile? = null

    fun refresh() {
        DumbService.getInstance(project).runWhenSmart {
            val newTemplates = GradlePropertiesLoader.findTemplates(project)
            val newGradleProps = GradlePropertiesLoader.findGradleProperties(project)

            templates = newTemplates
            gradleProperties = newGradleProps

            _stateUpdateCounter.value++
        }
    }

    fun getTemplates(): List<VirtualFile> = templates
    fun getGradleProperties(): VirtualFile? = gradleProperties

    companion object {
        fun getInstance(project: Project): GradlePropertiesStateService =
            project.getService(GradlePropertiesStateService::class.java)
    }
}