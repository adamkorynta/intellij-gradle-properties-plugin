package com.geiconsultants.intellij.gradle.properties.plugin

import com.intellij.openapi.vfs.VirtualFile

object GradlePropertiesParser {

    fun parse(file: VirtualFile): Map<String, String> {
        val result = mutableMapOf<String, String>()

        file.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                val trimmed = line.trim()
                if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("!")) {
                    return@forEach
                }

                if ('=' in trimmed) {
                    val (key, value) = trimmed.split("=", limit = 2)
                    result[key.trim()] = value.trim()
                }
            }
        }

        return result
    }
}