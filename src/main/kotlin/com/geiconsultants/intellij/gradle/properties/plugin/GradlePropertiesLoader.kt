package com.geiconsultants.intellij.gradle.properties.plugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile

object GradlePropertiesLoader {
    private const val PREFIX = "gradle.properties."
    private const val EXACT = "gradle.properties"

    fun findTemplates(project: Project): List<VirtualFile> {
        val results = mutableListOf<VirtualFile>()

        val roots = ProjectRootManager.getInstance(project).contentRoots

        for (root in roots) {
            collect(root, results)
        }

        return results.sortedBy { it.path }
    }

    fun findGradleProperties(project: Project): VirtualFile? {
        val roots = ProjectRootManager.getInstance(project).contentRoots

        for (root in roots) {
            root.findChild(EXACT)?.let { return it }
        }
        return null
    }

    private fun collect(dir: VirtualFile, results: MutableList<VirtualFile>) {
        if (!dir.isDirectory) return

        for (child in dir.children) {
            if (child.isDirectory) {
                collect(child, results)
            } else {
                val name = child.name
                if (name.startsWith(PREFIX) && name != EXACT) {
                    results.add(child)
                }
            }
        }
    }
}
