package com.geiconsultants.intellij.gradle.properties.plugin

import com.intellij.openapi.vfs.VirtualFile

object GradlePropertiesTemplateParser {

    fun parse(template: VirtualFile): Map<String, Pair<String?, GradlePropertyDoc>> {
        val result = linkedMapOf<String, Pair<String?, GradlePropertyDoc>>()
        val pendingDocLines = mutableListOf<String>()

        template.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { rawLine ->
                val line = rawLine.trim()

                when {
                    line.startsWith("#") || line.startsWith("!") -> {
                        val comment = line
                            .removePrefix("#")
                            .removePrefix("!")
                            .trim()

                        if (comment.startsWith("@")) {
                            pendingDocLines.add(comment)
                        }
                    }
                    '=' in line && !line.startsWith("#") && !line.startsWith("!") -> {
                        val (key, value) = line.split("=", limit = 2)
                        val doc = parseDocBlock(pendingDocLines)

                        result[key.trim()] = value.trim() to doc
                        pendingDocLines.clear()
                    }
                    else -> {
                        pendingDocLines.clear()
                    }
                }
            }
        }

        return result
    }

    private fun parseDocBlock(lines: List<String>): GradlePropertyDoc {
        var description = ""
        var type: String? = "string"

        for (line in lines) {
            when {
                line.startsWith("@doc") ->
                    description = line.removePrefix("@doc").trim()

                line.startsWith("@type") ->
                    type = line.removePrefix("@type").trim()
            }
        }

        return GradlePropertyDoc(
            description = description,
            type = type
        )
    }
}
