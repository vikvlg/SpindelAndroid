package ru.vik.spindel.core.designsystem.component.datatable

import androidx.compose.animation.core.animateFloatAsState
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
import ru.vik.spindel.core.designsystem.component.datatable.internal.DataTableSpec
import ru.vik.spindel.core.designsystem.component.datatable.internal.ResolvedColumn

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
    // TODO: Расчет ширины экрана и возможность растянуть Flexed столбцы.
    var pinnedWidth = Dp(0f)
    columns.forEachPinnedColumn(fixedColumnCount) { column ->
        when (column.width) {
            is DataTableColumn.Width.Fixed -> pinnedWidth += column.width.fixedWidth
            is DataTableColumn.Width.Flexed -> throw Exception("Fixed columns require a fixed width. Column '${column.title}' has a flex width.")
        }
    }

    val spec = DataTableSpec(
        pinnedColumns = columns.filterPinnedColumn(fixedColumnCount).map { ResolvedColumn(it, it.calcWidth()) },
        scrollableColumns = columns.filterUnpinnedColumn(fixedColumnCount).map { ResolvedColumn(it, it.calcWidth()) },
        fixedRowHeader = fixedRowHeader,
        //pinnedWidth = pinnedWidth,
        headerHeight = headerHeight,
        rowHeight = rowHeight,
        cellPadding = cellPadding,
        gridLines = gridLines,
        gridLineThickness = gridLineThickness,
        colors = colors,
        headerTextStyle = headerTextStyle,
        cellTextStyle = cellTextStyle,
        scrollState = scrollState,
        overscroll = overscroll
    )

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
        spec.FixedHeader()

        LazyColumn {
            item {
                spec.FlexedHeader()
            }

            items(items = items, key = key) { item ->
                spec.Row(item)
            }
        }
    }
}

/** Фиксированный заголовок таблицы. **/
@Composable
private fun <T> DataTableSpec<T>.FixedHeader() {
    if (fixedRowHeader) {
        Header()
    }
}

/** Плавающий заголовок таблицы. **/
@Composable
private fun <T> DataTableSpec<T>.FlexedHeader() {
    if (!fixedRowHeader) {
        Header()
    }
}

/** Заголовок таблицы. **/
@Composable
private fun <T> DataTableSpec<T>.Header() {
    Row(
        modifier = Modifier
            .height(headerHeight)
            .background(colors.headerBackground)
    ) {
        // Ячейки фиксированных столбцов
        pinnedColumns.forEach { column ->
            HeaderCell(column)
        }
        // Ячейки плавающих столбцов
        val rowOverscroll = remember(overscroll) { overscroll?.withoutVisualEffect() }
        Row(modifier = Modifier
            .fillMaxWidth()
            .rowGridLine(gridLines, colors.divider, gridLineThickness)
            // Переведем овер-скролл строки на всю таблицу.
            .horizontalScroll(state = scrollState, overscrollEffect = rowOverscroll)
        ) {
            scrollableColumns.forEach { column ->
                HeaderCell(column)
            }
        }
    }
}

/** Ячейка заголовка таблицы. **/
@Composable
private fun <T> DataTableSpec<T>.HeaderCell(
    column: ResolvedColumn<T>,
) {
    Text(
        modifier = Modifier
            .height(headerHeight)
            .width(column.width)
            .padding(cellPadding),
        text = column.column.title,
        textAlign = column.column.titleAlign,
        color = colors.headerContent,
        style = headerTextStyle,
    )
}

/** Строка таблицы. **/
@Composable
private fun <T> DataTableSpec<T>.Row(
    row: T,
) {
    Row(modifier = Modifier
        .height(rowHeight)
        .background(colors.rowBackground)
    ) {
        // Закрепленные столбцы, если они есть
        pinnedColumns.forEach { column ->
            Cell(
                item = row,
                column = column,
                isFixed = true,
            )
        }

        // Плавающие столбцы
        val rowOverscroll = remember(overscroll) { overscroll?.withoutVisualEffect() }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .rowGridLine(gridLines, colors.divider, gridLineThickness)
                // Переведем овер-скролл строки на всю таблицу.
                .horizontalScroll(state = scrollState, overscrollEffect = rowOverscroll)
        ) {
            scrollableColumns.forEach { column ->
                Cell(
                    item = row,
                    column = column,
                    isFixed = false,
                )
            }
        }
    }
}

/** Ячейка тела таблицы. **/
@Composable
private fun <T> DataTableSpec<T>.Cell(
    item: T,
    column: ResolvedColumn<T>,
    isFixed: Boolean,
) {
    val background = if (isFixed) colors.pinnedBackground else colors.rowBackground
    val color = if (isFixed) colors.pinnedCellContent else colors.unpinnedCellContent

    Text(
        modifier = Modifier
            .height(rowHeight)
            .width(column.width)
            .background(background)
            .cellGridLine(gridLines, colors.divider, gridLineThickness)
            // WARNING: padding обязательно должен быть после cellGridLine, чтобы не ломать отрисовку сетки.
            .padding(cellPadding),
        text = column.column.text(item),
        textAlign = column.column.textAlign,
        style = cellTextStyle,
        color = color,
    )
}

/** Рисует нижнюю линию всей строки. **/
private fun Modifier.rowGridLine(
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
private fun Modifier.cellGridLine(
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
private fun Modifier.horizontalScrollShadows(
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

/** Проходит все плавающие столбцы таблицы. **/
private inline fun <T> List<DataTableColumn<T>>.forEachUnpinnedColumn(
    fixedColumnCount: Int,
    block: (DataTableColumn<T>) -> Unit
) {
    forEachIndexed { index, column ->
        if (index >= fixedColumnCount)
            block(column)
    }
}

/** Возвращает список закрепленных столбцов. **/
private fun <T> List<DataTableColumn<T>>.filterPinnedColumn(
    fixedColumnCount: Int
): List<DataTableColumn<T>> = filterIndexed { index, _ -> index < fixedColumnCount }

/** Возвращает список плавающих столбцов. **/
private fun <T> List<DataTableColumn<T>>.filterUnpinnedColumn(
    fixedColumnCount: Int
): List<DataTableColumn<T>> = filterIndexed { index, _ -> index >= fixedColumnCount }

private fun <T> DataTableColumn<T>.calcWidth(): Dp {
    return when (width) {
        is DataTableColumn.Width.Fixed -> width.fixedWidth
        is DataTableColumn.Width.Flexed -> width.minWidth
    }
}