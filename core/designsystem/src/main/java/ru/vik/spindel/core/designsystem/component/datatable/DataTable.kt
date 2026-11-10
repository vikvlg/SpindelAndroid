package ru.vik.spindel.core.designsystem.component.datatable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

@Composable
fun <T> DataTable(
    items: List<T>,
    key: (T) -> Any,
    columns: List<DataTableColumn<T>>,
    fixedColumnCount: Int = 1,
    fixedRowHeader: Boolean = true,
    colors: DataTableColors = DataTableDefaults.colors(),
    headerHeight: Dp = DataTableDefaults.HeaderHeight,
    rowHeight: Dp = DataTableDefaults.RowHeight,
    cellPadding: PaddingValues = DataTableDefaults.CellPadding,
    gridLines: DataTableGridLines = DataTableDefaults.GridLines,
    gridLineThickness: Dp = DataTableDefaults.GridLineThickness,
    headerTextStyle: TextStyle = DataTableDefaults.headerTextStyle,
    cellTextStyle: TextStyle = DataTableDefaults.cellTextStyle,
) {
    // Скроллы и эффект овер-скролла.
    val scrollState = rememberScrollState()
    val overscroll = rememberOverscrollEffect()

    // Расчет ширины закрепленных столбцов
    var pinnedWidth = Dp(0f)
    columns.forEachPinnedColumn(fixedColumnCount) { column ->
        when (column.width) {
            is DataTableColumn.Width.Fixed -> pinnedWidth += column.width.fixedWidth
            is DataTableColumn.Width.Flexed -> throw Exception("Fixed columns require a fixed width. Column '${column.title}' has a flex width.")
        }
    }
    // TODO: Расчет ширины экрана и возможность растянуть Flexed столбцы.

    Column(
        modifier = Modifier
            .fillMaxSize()
            // Общий эффект овер-скролла на всю таблицу
            .overscroll(overscroll)
            .horizontalScrollShadows(
                state = scrollState,
                pinnedWidth = pinnedWidth,
                startShadowColor = colors.startShadow,
                endShadowColor = colors.endShadow,
            )
    ) {
        if (fixedRowHeader) {
            // Фиксированный заголовок
            TableHeader(
                scrollState = scrollState,
                overscroll = overscroll,
                columns = columns,
                fixedColumnCount = fixedColumnCount,
                headerHeight = headerHeight,
                textStyle = headerTextStyle,
                cellPadding = cellPadding,
                colors = colors,
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
                        headerHeight = headerHeight,
                        textStyle = headerTextStyle,
                        cellPadding = cellPadding,
                        colors = colors,
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
                            rowHeight = rowHeight,
                            textStyle = cellTextStyle,
                            gridLineThickness = gridLineThickness,
                            cellPadding = cellPadding,
                            gridLines = gridLines,
                            colors = colors,
                        )
                    }
                    // Обычные столбцы
                    val rowOverscroll = remember(overscroll) { overscroll?.withoutVisualEffect() }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .rowGridLine(gridLines, colors.divider, gridLineThickness)
                            // Отключим эффект овер-скролла, т.к. он работает только для одной строки и плохо выглядит
                            .horizontalScroll(state = scrollState, overscrollEffect = rowOverscroll)
                    ) {
                        columns.forEachUnpinnedColumn(fixedColumnCount) { column ->
                            TableColumn(
                                item = item,
                                column = column,
                                isFixed = false,
                                rowHeight = rowHeight,
                                textStyle = cellTextStyle,
                                gridLineThickness = gridLineThickness,
                                cellPadding = cellPadding,
                                gridLines = gridLines,
                                colors = colors,
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
    headerHeight: Dp,
    textStyle: TextStyle,
    cellPadding: PaddingValues,
    colors: DataTableColors
) {
    Row(
        modifier = Modifier
            .height(headerHeight)
            .background(colors.headerBackground)
    ) {
        // Ячейки фиксированных столбцов
        columns.forEachPinnedColumn(fixedColumnCount) { column ->
            TableHeaderCell(
                column = column,
                rowHeight = headerHeight,
                textStyle = textStyle,
                cellPadding = cellPadding,
                colors = colors,
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
                    rowHeight = headerHeight,
                    textStyle = textStyle,
                    cellPadding = cellPadding,
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun <T> TableHeaderCell(
    column: DataTableColumn<T>,
    rowHeight: Dp,
    textStyle: TextStyle,
    cellPadding: PaddingValues,
    colors: DataTableColors,
) {
    val width = when (column.width) {
        is DataTableColumn.Width.Fixed -> column.width.fixedWidth
        is DataTableColumn.Width.Flexed -> column.width.minWidth
    }

    Text(
        modifier = Modifier
            .height(rowHeight)
            .width(width)
            .padding(cellPadding),
        text = column.title,
        textAlign = column.titleAlign,
        color = colors.headerContent,
        style = textStyle,
    )
}

@Composable
fun <T> TableColumn(
    item: T,
    column: DataTableColumn<T>,
    isFixed: Boolean,
    rowHeight: Dp,
    textStyle: TextStyle,
    gridLineThickness: Dp,
    cellPadding: PaddingValues,
    gridLines: DataTableGridLines,
    colors: DataTableColors,
) {
    val columnWidth = when (column.width) {
        is DataTableColumn.Width.Fixed -> column.width.fixedWidth
        is DataTableColumn.Width.Flexed -> column.width.minWidth
    }
    val background = if (isFixed) colors.pinnedBackground else colors.rowBackground
    val color = if (isFixed) colors.pinnedCellContent else colors.unpinnedCellContent

    Text(
        modifier = Modifier
            .height(rowHeight)
            .width(columnWidth)
            .background(background)
            .cellGridLine(gridLines, colors.divider, gridLineThickness)
            // WARNING: padding обязательно должен быть после cellGridLine, чтобы не ломать отрисовку сетки.
            .padding(cellPadding),
        text = column.text(item),
        textAlign = column.textAlign,
        style = textStyle,
        color = color,
    )
}

/** Рисует нижнюю линию всей строки. **/
internal fun Modifier.rowGridLine(
    gridLines: DataTableGridLines,
    color: Color,
    thickness: Dp,
): Modifier =
    if (!gridLines.horizontal) this
    else drawWithContent {
        drawContent()
        val stroke = thickness.toPx()
        val y = size.height - stroke / 2
        drawLine(color, Offset(0f, y), Offset(size.width, y), stroke)
    }

/** Рисует правую линию ячейки. **/
internal fun Modifier.cellGridLine(
    gridLines: DataTableGridLines,
    color: Color,
    thickness: Dp,
): Modifier =
    if (!gridLines.vertical) this
    else drawWithContent {
        drawContent()
        val stroke = thickness.toPx()
        val x = size.width - stroke / 2
        drawLine(color, Offset(x, 0f), Offset(x, size.height), stroke)
    }

/** Рисует тени для горизонтального скролла. **/
@Composable
internal fun Modifier.horizontalScrollShadows(
    state: ScrollState,
    pinnedWidth: Dp,
    startShadowColor: Color,
    endShadowColor: Color,
    startShadowWidth: Dp = DataTableDefaults.StartShadowWidth,
    endShadowWidth: Dp = DataTableDefaults.EndShadowWidth,
): Modifier {
    val showStart by remember(state) { derivedStateOf { state.canScrollBackward } }
    val showEnd by remember(state) { derivedStateOf { state.canScrollForward } }
    val startAlpha by animateFloatAsState(if (showStart) 1f else 0f, label = "startShadow")
    val endAlpha by animateFloatAsState(if (showEnd) 1f else 0f, label = "endShadow")

    return drawWithContent {
        drawContent()
        val startW = startShadowWidth.toPx()
        val endW = endShadowWidth.toPx()

        if (startAlpha > 0f) {
            val x = pinnedWidth.toPx()
            drawRect(
                brush = Brush.horizontalGradient(listOf(startShadowColor, Color.Transparent), startX = x, endX = x + startW),
                topLeft = Offset(x, 0f),
                size = Size(startW, size.height),
                alpha = startAlpha,
            )
        }
        if (endAlpha > 0f) {
            val x = size.width - endW
            drawRect(
                brush = Brush.horizontalGradient(listOf(Color.Transparent, endShadowColor), startX = x, endX = size.width),
                topLeft = Offset(x, 0f),
                size = Size(endW, size.height),
                alpha = endAlpha,
            )
        }
    }
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