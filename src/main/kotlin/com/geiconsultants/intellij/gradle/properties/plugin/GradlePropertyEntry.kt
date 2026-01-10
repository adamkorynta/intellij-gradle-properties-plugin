package com.geiconsultants.intellij.gradle.properties.plugin

data class GradlePropertyEntry(
    val key: String,
    val value: String?,
    val description: String,
    val type: String?
)
