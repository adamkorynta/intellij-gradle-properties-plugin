package com.geiconsultants.intellij.gradle.properties.plugin

import javax.swing.*
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

class TypeAwareValueCell(table: JTable, private val model: GradlePropertiesTableModel) :
    AbstractCellEditor(), TableCellRenderer, TableCellEditor {

    private enum class Mode { BOOLEAN, DELEGATE }

    private val booleanComponent = JCheckBox().apply {
        horizontalAlignment = SwingConstants.CENTER
        isOpaque = true
    }

    private val fallbackStringRenderer = table.getDefaultRenderer(String::class.java)
    private val fallbackAnyRenderer = table.getDefaultRenderer(Any::class.java)

    private val fallbackStringEditor = table.getDefaultEditor(String::class.java) ?: DefaultCellEditor(JTextField())
    private val fallbackAnyEditor = table.getDefaultEditor(Any::class.java) ?: fallbackStringEditor

    private var mode: Mode = Mode.DELEGATE
    private var delegateEditor: TableCellEditor = fallbackStringEditor

    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any?,
        isRowSelected: Boolean,
        hasFocus: Boolean,
        viewRow: Int,
        viewColumn: Int
    ): JComponent {

        if (viewColumn == 1) {
            val modelRow = table.convertRowIndexToModel(viewRow)
            val entry = model.getEntry(modelRow)

            if (entry.type?.equals("boolean", ignoreCase = true) == true) {
                booleanComponent.isSelected =
                    value?.toString()?.equals("true", ignoreCase = true) == true

                booleanComponent.background =
                    if (isRowSelected) table.selectionBackground else table.background
                booleanComponent.foreground =
                    if (isRowSelected) table.selectionForeground else table.foreground

                return booleanComponent
            }
        }

        val renderer = when (value) {
            is String -> fallbackStringRenderer
            else -> fallbackAnyRenderer
        }

        val component = renderer.getTableCellRendererComponent(
            table, value, isRowSelected, hasFocus, viewRow, viewColumn
        ) as JComponent

        component.toolTipText = value?.toString()
        return component
    }

    override fun getTableCellEditorComponent(
        table: JTable,
        value: Any?,
        isRowSelected: Boolean,
        viewRow: Int,
        viewColumn: Int
    ): JComponent {

        if (viewColumn == 1) {
            val modelRow = table.convertRowIndexToModel(viewRow)
            val entry = model.getEntry(modelRow)

            if (entry.type?.equals("boolean", ignoreCase = true) == true) {
                mode = Mode.BOOLEAN

                booleanComponent.isSelected =
                    value?.toString()?.equals("true", ignoreCase = true) == true

                booleanComponent.background =
                    if (isRowSelected) table.selectionBackground else table.background
                booleanComponent.foreground =
                    if (isRowSelected) table.selectionForeground else table.foreground

                return booleanComponent
            }
        }

        mode = Mode.DELEGATE
        delegateEditor = when (value) {
            is String -> fallbackStringEditor
            else -> fallbackAnyEditor
        }
        val editorComponent = delegateEditor.getTableCellEditorComponent(
            table, value, isRowSelected, viewRow, viewColumn
        ) as JComponent
        installCommitOnEnter(editorComponent)
        return editorComponent
    }

    override fun getCellEditorValue(): Any {
        return when (mode) {
            Mode.BOOLEAN -> booleanComponent.isSelected.toString()
            Mode.DELEGATE -> {
                val value = delegateEditor.cellEditorValue
                if (value == null || value == "") {
                    // If the delegate says it's empty, let's double check the actual component
                    val editorComp = (delegateEditor as? DefaultCellEditor)?.component
                    if (editorComp is javax.swing.text.JTextComponent) {
                        return editorComp.text
                    }
                }
                value ?: ""
            }
        }
    }

    private fun installCommitOnEnter(component: JComponent) {
        component.getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke("ENTER"), "commit")

        component.actionMap.put("commit", object : AbstractAction() {
            override fun actionPerformed(e: java.awt.event.ActionEvent?) {
                stopCellEditing()
            }
        })
    }
}
