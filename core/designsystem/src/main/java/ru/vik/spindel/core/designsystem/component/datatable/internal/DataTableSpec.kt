package ru.vik.spindel.core.designsystem.component.datatable.internal

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import ru.vik.spindel.core.designsystem.component.datatable.DataTableColors
import ru.vik.spindel.core.designsystem.component.datatable.DataTableColumn
import ru.vik.spindel.core.designsystem.component.datatable.DataTableGridLines
import ru.vik.spindel.core.designsystem.component.datatable.DataTableState
import ru.vik.spindel.core.designsystem.component.datatable.GestureCallback

/** UI-параметры таблицы. **/
@Immutable
internal data class DataTableSpec<T>(
    val pinnedColumns: List<ResolvedColumn<T>>,
    val scrollableColumns: List<ResolvedColumn<T>>,
    val fixedRowHeader: Boolean,
    val onRowClick: GestureCallback<T>,
    val onRowLongClick: GestureCallback<T>,
    //val pinnedWidth: Dp,
    val headerHeight: Dp,
    val rowHeight: Dp,
    val cellPadding: PaddingValues,
    val gridLines: DataTableGridLines,
    val gridLineThickness: Dp,
    val colors: DataTableColors,
    val headerTextStyle: TextStyle,
    val cellTextStyle: TextStyle,
    val headerVerticalAlign: Alignment.Vertical,
    val cellVerticalAlign: Alignment.Vertical,
    val tableState: DataTableState,
)

/** UI-параметры столбца таблицы. **/
internal data class ResolvedColumn<T>(
    val column: DataTableColumn<T>,
    val width: Dp,
)
