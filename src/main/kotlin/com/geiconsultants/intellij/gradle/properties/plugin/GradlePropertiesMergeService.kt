package com.geiconsultants.intellij.gradle.properties.plugin

import com.intellij.openapi.vfs.VirtualFile

object GradlePropertiesMergeService {

    fun merge(
        template: VirtualFile,
        gradleProperties: VirtualFile?
    ): List<GradlePropertyEntry> {

        val templateData =
            GradlePropertiesTemplateParser.parse(template)

        val actualValues =
            gradleProperties?.let { GradlePropertiesParser.parse(it) }
                ?: emptyMap()

        val templateEntries = templateData.map { (key, pair) ->
            val (_, doc) = pair

            GradlePropertyEntry(
                key = key,
                value = actualValues[key],
                description = doc.description,
                type = doc.type
            )
        }

        val missingFromTemplate = actualValues.keys
            .filter { it !in templateData }
            .map { key ->
                GradlePropertyEntry(
                    key = key,
                    value = actualValues[key],
                    description = "",
                    type = null
                )
            }

        return templateEntries + missingFromTemplate
    }
}
