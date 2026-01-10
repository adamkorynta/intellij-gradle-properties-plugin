package com.geiconsultants.intellij.gradle.properties.plugin

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.table.JBTable
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JPanel
import javax.swing.JTable

class GradlePropertiesPanel(private val project: Project) : JPanel(BorderLayout()) {

    private val tableModel = GradlePropertiesTableModel()
    private val table = JBTable(tableModel)

    private val descriptionArea = JBTextArea().apply {
        isEditable = false
        lineWrap = true
        wrapStyleWord = true
    }

    init {
        // ---------------- Toolbar ----------------
        val actionGroup = DefaultActionGroup().apply {
            add(LoadGradlePropertiesAction(this@GradlePropertiesPanel))
            add(SaveGradlePropertiesAction(this@GradlePropertiesPanel))
        }

        val toolbar = ActionManager.getInstance().createActionToolbar(
            ActionPlaces.TOOLWINDOW_CONTENT,
            actionGroup,
            true
        )
        toolbar.setTargetComponent(this)

        table.autoResizeMode = JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
        table.rowHeight = 24
        table.fillsViewportHeight = true

        val typeAwareCell = TypeAwareValueCell(table, tableModel)
        table.setDefaultRenderer(Any::class.java, typeAwareCell)
        table.setDefaultEditor(Any::class.java, typeAwareCell)

        wireDescriptionPanel()

        val tableScroll = JBScrollPane(table)
        val descriptionScroll = JBScrollPane(descriptionArea).apply {
            preferredSize = Dimension(0, 140)
        }

        val centerPanel = JPanel(BorderLayout()).apply {
            add(tableScroll, BorderLayout.CENTER)
            add(descriptionScroll, BorderLayout.SOUTH)
        }

        add(toolbar.component, BorderLayout.NORTH)
        add(centerPanel, BorderLayout.CENTER)

        refreshFromState()
        setupStateObservation()
    }

    fun refreshFromState() {
        ApplicationManager.getApplication().invokeLater {
            val state = GradlePropertiesStateService.getInstance(project)
            val templates = state.getTemplates()
            val gradleProps = state.getGradleProperties()

            if (templates.isEmpty()) {
                tableModel.setEntries(emptyList())
                descriptionArea.text = ""
                return@invokeLater
            }

            val merged = GradlePropertiesMergeService.merge(
                template = templates.first(),
                gradleProperties = gradleProps
            )

            tableModel.setEntries(merged)
            descriptionArea.text = ""
        }
    }

    private fun wireDescriptionPanel() {
        table.selectionModel.addListSelectionListener {
            val viewRow = table.selectedRow
            if (viewRow < 0) {
                descriptionArea.text = ""
                return@addListSelectionListener
            }

            val modelRow = table.convertRowIndexToModel(viewRow)
            val entry = tableModel.getEntry(modelRow)
            descriptionArea.text = entry.description
            descriptionArea.caretPosition = 0
        }
    }

    fun getEntries(): List<GradlePropertyEntry> {
        if (table.isEditing) {
            table.cellEditor?.stopCellEditing()
        }
        return tableModel.getEntries()
    }

    private fun setupStateObservation() {
        val service = GradlePropertiesStateService.getInstance(project)

        // Using projectModelScope or similar is often safer in newer versions,
        // but for now, this will allow the 'getService' error to disappear.
        MainScope().launch {
            service.stateUpdateEvents.collect {
                refreshFromState()
            }
        }
    }
}
