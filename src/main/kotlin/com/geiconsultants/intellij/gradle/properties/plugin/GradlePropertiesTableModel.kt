package com.geiconsultants.intellij.gradle.properties.plugin

import javax.swing.table.AbstractTableModel

class GradlePropertiesTableModel : AbstractTableModel() {

    private val columns = arrayOf(
        "Key",
        "Value"
    )

    private var rows: List<GradlePropertyEntry> = emptyList()

    fun setEntries(entries: List<GradlePropertyEntry>) {
        rows = entries
        fireTableDataChanged()
    }

    fun getEntry(row: Int): GradlePropertyEntry = rows[row]

    override fun getRowCount() = rows.size
    override fun getColumnCount() = columns.size
    override fun getColumnName(column: Int) = columns[column]

    override fun isCellEditable(rowIndex: Int, columnIndex: Int) =
        columnIndex == 1 // Value column only

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val row = rows[rowIndex]
        return when (columnIndex) {
            0 -> row.key
            1 -> row.value ?: ""
            else -> ""
        }
    }

    override fun setValueAt(value: Any?, rowIndex: Int, columnIndex: Int) {
        if (columnIndex != 1) return
        val row = rows[rowIndex]
        rows = rows.toMutableList().also {
            it[rowIndex] = row.copy(value = value?.toString())
        }
        fireTableCellUpdated(rowIndex, columnIndex)
    }

    fun getEntries(): List<GradlePropertyEntry> = rows
}
