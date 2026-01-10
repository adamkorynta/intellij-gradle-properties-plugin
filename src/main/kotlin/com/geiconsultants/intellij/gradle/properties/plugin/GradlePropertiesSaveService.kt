package com.geiconsultants.intellij.gradle.properties.plugin

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.nio.charset.StandardCharsets

object GradlePropertiesSaveService {

    fun buildUpdatedContent(
        project: Project,
        entries: List<GradlePropertyEntry>
    ): Pair<VirtualFile, String>? {

        val state = GradlePropertiesStateService.getInstance(project)
        val gradleFile = state.getGradleProperties() ?: return null

        val originalText = readFile(gradleFile)
        val updatedText = merge(originalText, entries)

        return gradleFile to updatedText
    }

    fun apply(
        project: Project,
        file: VirtualFile,
        newContent: String
    ) {
        WriteCommandAction.runWriteCommandAction(project, "Save gradle.properties", null, {
            file.setBinaryContent(newContent.toByteArray(StandardCharsets.UTF_8))
            FileDocumentManager.getInstance().reloadFiles(file)
        })
    }

    // ------------------------------------------------------------
    // Merge logic
    // ------------------------------------------------------------
    private fun merge(
        original: String,
        entries: List<GradlePropertyEntry>
    ): String {
        val lines = original.lines().toMutableList()

        // Map of key -> new value (ignore blank)
        val updates = entries
            .filter { !it.value.isNullOrBlank() }
            .associate { it.key to it.value!!.trim() }
            .toMutableMap()

        val keyRegex = Regex("""^\s*([^#!\s][^=:\s]*)\s*[=:].*$""")

        for (i in lines.indices) {
            val line = lines[i]
            val match = keyRegex.matchEntire(line) ?: continue

            val key = match.groupValues[1]
            val newValue = updates.remove(key) ?: continue

            lines[i] = "$key=$newValue"
        }

        // Append remaining (new) properties
        if (updates.isNotEmpty()) {
            if (lines.isNotEmpty() && lines.last().isNotBlank()) {
                lines.add("")
            }

            updates.forEach { (key, value) ->
                lines.add("$key=$value")
            }
        }

        return lines.joinToString("\n")
    }

    private fun readFile(file: VirtualFile): String =
        String(file.contentsToByteArray(), StandardCharsets.UTF_8)
}
