package com.geiconsultants.intellij.gradle.properties.plugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class GradlePropertiesStartupActivity : ProjectActivity {


    override suspend fun execute(project: Project) {
        GradlePropertiesStateService
            .getInstance(project)
            .refresh()
    }
}