package ru.vik.spindel.core.designsystem.component.datatable

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class DataTableColors(
    val headerBackground: Color,
    val pinnedBackground: Color,
    val rowBackground: Color,
    val selectedRowBackground: Color,
    val headerContent: Color,
    val pinnedCellContent: Color,
    val unpinnedCellContent: Color,
    val divider: Color,
    val startShadow: Color,
    val endShadow: Color,
)
