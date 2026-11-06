package ru.vik.spindel.core.designsystem.component.datatable

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun <T>DataTable(
    items: List<T>,
    key: (T) -> Any,
    columns: List<DataTableColumn<T>>,
    fixedColumnCount: Int = 1,
    fixedRowHeader: Boolean = true
) {
    // TODO: Android12+: отключить overscroll (через CompositionLocal?).
    val scrollState = rememberScrollState()
    // TODO: Конфиг.
    val cellHorizontalPadding = 4.dp
    val strokeWidth: Dp = 1.dp
    val gridLineColor = MaterialTheme.colorScheme.surfaceVariant

    // TODO: Расчет ширины экрана и возможность растянуть Flexed столбцы.
    // TODO: Столбцы fixedColumnCount нужно выносить перед Row, чтобы они не попадали в скролл.

    Column(modifier = Modifier.fillMaxSize())
    {
        if (fixedRowHeader) {
            // Фиксированный заголовок
            TableHeader(
                scrollState = scrollState,
                columns = columns,
                isFixed = true,
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
                        columns = columns,
                        isFixed = true,
                        cellHorizontalPadding = cellHorizontalPadding
                    )
                }
            }

            // Строки таблицы
            items(items = items, key = key) { item ->
                Row(
                    modifier = Modifier
                        .height(24.dp)
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
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
                    for (column in columns) {
                        val index = columns.indexOf(column)
                        var modifier: Modifier = Modifier
                        modifier = when (column.width) {
                            is DataTableColumn.Width.Fixed -> modifier.width(column.width.fixedWidth)
                            is DataTableColumn.Width.Flexed -> modifier.width(column.width.minWidth)
                        }
                        if (index < fixedColumnCount) {
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
                        val color = if (index < fixedColumnCount) MaterialTheme.colorScheme.onSurfaceVariant else Color.Unspecified
                        Text(
                            text = column.text(item),
                            textAlign = column.textAlign,
                            modifier = modifier,
                            color = color
                        )
                    }
                }
            } // items
        }
    }
}

@Composable
fun <T>TableHeader(
    scrollState: ScrollState,
    columns: List<DataTableColumn<T>>,
    isFixed: Boolean,
    cellHorizontalPadding: Dp
) {
    var rowModifier: Modifier = Modifier
        .height(24.dp)
        .fillMaxWidth()
        .horizontalScroll(scrollState)
    if (isFixed) {
        rowModifier = rowModifier.background(MaterialTheme.colorScheme.surfaceVariant)
    }

    Row(
        modifier = rowModifier
    ) {
        Row {
            for (column in columns) {
                var cellModifier: Modifier = Modifier
                cellModifier = when (column.width) {
                    is DataTableColumn.Width.Fixed -> cellModifier.width(column.width.fixedWidth)
                    is DataTableColumn.Width.Flexed -> cellModifier.width(column.width.minWidth)
                }
                cellModifier = cellModifier.padding(horizontal = cellHorizontalPadding)
                val color = if (isFixed) MaterialTheme.colorScheme.onSurfaceVariant else Color.Unspecified
                Text(
                    text = column.title,
                    textAlign = column.titleAlign,
                    modifier = cellModifier, color = color
                )
            }
        }
    }
}
