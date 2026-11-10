package ru.vik.spindel.core.designsystem.component.datatable

import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.withoutVisualEffect
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun <T> DataTable(
    items: List<T>,
    key: (T) -> Any,
    columns: List<DataTableColumn<T>>,
    fixedColumnCount: Int = 1,
    fixedRowHeader: Boolean = true
) {
    // Скроллы и эффект овер-скролла.
    val scrollState = rememberScrollState()
    val overscroll = rememberOverscrollEffect()
    // TODO: Конфиг.
    val cellHorizontalPadding = 4.dp
    val strokeWidth: Dp = 1.dp
    val gridLineColor = MaterialTheme.colorScheme.surfaceVariant
    val rowHeight: Dp = 24.dp

    // TODO: Расчет ширины экрана и возможность растянуть Flexed столбцы.
    // TODO: Столбцы fixedColumnCount нужно выносить перед Row, чтобы они не попадали в скролл.

    Column(
        modifier = Modifier
            .fillMaxSize()
            // Общий эффект овер-скролла на всю таблицу
            .overscroll(overscroll)
    ) {
        if (fixedRowHeader) {
            // Фиксированный заголовок
            TableHeader(
                scrollState = scrollState,
                overscroll = overscroll,
                columns = columns,
                fixedColumnCount = fixedColumnCount,
                rowHeight = rowHeight,
                cellHorizontalPadding = cellHorizontalPadding
            )
        }

        // Тело таблицы
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            if (!fixedRowHeader) {
                // Плавающий заголовок
                item {
                    TableHeader(
                        scrollState = scrollState,
                        overscroll = overscroll,
                        columns = columns,
                        fixedColumnCount = fixedColumnCount,
                        rowHeight = rowHeight,
                        cellHorizontalPadding = cellHorizontalPadding
                    )
                }
            }

            // Строки таблицы
            items(items = items, key = key) { item ->
                Row(modifier = Modifier
                    .height(rowHeight)
                ) {
                    // Закрепленные столбцы, если они есть
                    columns.forEachPinnedColumn(fixedColumnCount) { column ->
                        TableColumn(
                            item = item,
                            column = column,
                            isFixed = true,
                            gridLineColor = gridLineColor,
                            strokeWidth = strokeWidth,
                            cellHorizontalPadding = cellHorizontalPadding,
                        )
                    }
                    // Обычные столбцы
                    val rowOverscroll = remember(overscroll) { overscroll?.withoutVisualEffect() }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            // Отключим эффект овер-скролла, т.к. он работает только для одной строки и плохо выглядит
                            .horizontalScroll(state = scrollState, overscrollEffect = rowOverscroll)
                            // Разметка таблицы: нижняя сетка строки.
                            .drawBehind {
                                drawLine(
                                    color = gridLineColor,
                                    start = Offset(0f, size.height),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = strokeWidth.toPx()
                                )
                            }
                    ) {
                        columns.forEachUnpinnedColumn(fixedColumnCount) { column ->
                            TableColumn(
                                item = item,
                                column = column,
                                isFixed = false,
                                gridLineColor = gridLineColor,
                                strokeWidth = strokeWidth,
                                cellHorizontalPadding = cellHorizontalPadding,
                            )
                        }
                    }
                }
            } // items
        }
    }
}

@Composable
fun <T> TableHeader(
    scrollState: ScrollState,
    overscroll: OverscrollEffect?,
    columns: List<DataTableColumn<T>>,
    fixedColumnCount: Int,
    rowHeight: Dp,
    cellHorizontalPadding: Dp
) {
    Row(
        modifier = Modifier
            .height(rowHeight)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Ячейки фиксированных столбцов
        columns.forEachPinnedColumn(fixedColumnCount) { column ->
            TableHeaderCell(
                column = column,
                cellHorizontalPadding = cellHorizontalPadding,
            )
        }
        // Ячейки нефиксированных столбцов
        val rowOverscroll = remember(overscroll) { overscroll?.withoutVisualEffect() }
        Row(modifier = Modifier
            .fillMaxWidth()
            // Отключим эффект овер-скролла, т.к. он работает только для одной строки и плохо выглядит
            .horizontalScroll(state = scrollState, overscrollEffect = rowOverscroll)
        ) {
            columns.forEachUnpinnedColumn(fixedColumnCount) { column ->
                TableHeaderCell(
                    column = column,
                    cellHorizontalPadding = cellHorizontalPadding,
                )
            }
        }
    }
}

@Composable
fun <T> TableHeaderCell(
    column: DataTableColumn<T>,
    cellHorizontalPadding: Dp,
) {
    var cellModifier: Modifier = Modifier
    cellModifier = when (column.width) {
        is DataTableColumn.Width.Fixed -> cellModifier.width(column.width.fixedWidth)
        is DataTableColumn.Width.Flexed -> cellModifier.width(column.width.minWidth)
    }
    cellModifier = cellModifier.padding(horizontal = cellHorizontalPadding)
    Text(
        text = column.title,
        textAlign = column.titleAlign,
        modifier = cellModifier, color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun <T> TableColumn(
    item: T,
    column: DataTableColumn<T>,
    isFixed: Boolean,
    gridLineColor: Color,
    strokeWidth: Dp,
    cellHorizontalPadding: Dp,
) {
    var modifier: Modifier = Modifier
    modifier = when (column.width) {
        is DataTableColumn.Width.Fixed -> modifier.width(column.width.fixedWidth)
        is DataTableColumn.Width.Flexed -> modifier.width(column.width.minWidth)
    }
    if (isFixed) {
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant)
    }
    modifier = modifier.drawBehind {
        // Разметка таблицы: правая линия ячейки.
        drawLine(
            color = gridLineColor,
            start = Offset(size.width, 0f),
            end = Offset(size.width, size.height),
            strokeWidth = strokeWidth.toPx()
        )
    }
    // WARNING: padding обязательно должен быть после drawBehind, чтобы не ломать отрисовку сетки.
    modifier = modifier.padding(horizontal = cellHorizontalPadding)
    val color = if (isFixed) MaterialTheme.colorScheme.onSurfaceVariant else Color.Unspecified
    Text(
        text = column.text(item),
        textAlign = column.textAlign,
        modifier = modifier,
        color = color
    )
}

/** Проходит все закрепленные столбцы таблицы. **/
private inline fun <T> List<DataTableColumn<T>>.forEachPinnedColumn(
    fixedColumnCount: Int,
    block: (DataTableColumn<T>) -> Unit
) {
    forEachIndexed { index, column ->
        if (index == fixedColumnCount) return
        block(column)
    }
}

/** Проходит все незакрепленные столбцы таблицы. **/
private inline fun <T> List<DataTableColumn<T>>.forEachUnpinnedColumn(
    fixedColumnCount: Int,
    block: (DataTableColumn<T>) -> Unit
) {
    forEachIndexed { index, column ->
        if (index >= fixedColumnCount)
            block(column)
    }
}