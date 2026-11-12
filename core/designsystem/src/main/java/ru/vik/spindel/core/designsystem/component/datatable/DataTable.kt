package ru.vik.spindel.core.designsystem.component.datatable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.withoutVisualEffect
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import ru.vik.spindel.core.designsystem.component.datatable.internal.DataTableSpec
import ru.vik.spindel.core.designsystem.component.datatable.internal.ResolvedColumn
import kotlin.math.max

@Composable
fun <T> DataTable(
    items: List<T>,
    key: (T) -> Any,
    columns: List<DataTableColumn<T>>,
    fixedColumnCount: Int = 1,
    fixedRowHeader: Boolean = true,
    onRowClick: GestureCallback<T> = null,
    onRowLongClick: GestureCallback<T> = null,
    tableState: DataTableState = rememberDataTableState(),
    colors: DataTableColors = DataTableDefaults.colors(),
    headerHeight: Dp = DataTableDefaults.HeaderHeight,
    rowHeight: Dp = DataTableDefaults.RowHeight,
    cellPadding: PaddingValues = DataTableDefaults.CellPadding,
    gridLines: DataTableGridLines = DataTableDefaults.GridLines,
    gridLineThickness: Dp = DataTableDefaults.GridLineThickness,
    headerTextStyle: TextStyle = DataTableDefaults.headerTextStyle,
    cellTextStyle: TextStyle = DataTableDefaults.cellTextStyle,
    headerVerticalAlign: Alignment.Vertical = DataTableDefaults.headerVerticalAlign,
    cellVerticalAlign: Alignment.Vertical = DataTableDefaults.cellVerticalAlign,
) {
    // Расчет ширины закрепленных столбцов
    // TODO: Расчет ширины экрана и возможность растянуть Flexed столбцы.
    // ИИ: Расчёт ширин оберните в remember.
    var pinnedWidth = Dp(0f)
    columns.forEachPinnedColumn(fixedColumnCount) { column ->
        when (column.width) {
            is DataTableColumn.Width.Fixed -> pinnedWidth += column.width.fixedWidth
            is DataTableColumn.Width.Flexed -> throw Exception("Fixed columns require a fixed width. Column '${column.title}' has a flex width.")
        }
    }

//    // TODO: Учитывать масштаб шрифта и увеличивать высоту строки.
//    val textMeasurer = rememberTextMeasurer()
//    val density = LocalDensity.current
//    val resolvedRowHeight =
//        remember(textMeasurer, density, cellTextStyle, rowHeight, cellPadding) {
//            val textHeightPx = textMeasurer.measure("Ag", cellTextStyle).size.height
//            with(density) {
//                val verticalPadding =
//                    cellPadding.calculateTopPadding() + cellPadding.calculateBottomPadding()
//                max(rowHeight, textHeightPx.toDp() + verticalPadding)
//            }
//        }

    val spec = DataTableSpec(
        pinnedColumns = columns.filterPinnedColumn(fixedColumnCount).map { ResolvedColumn(it, it.calcWidth()) },
        scrollableColumns = columns.filterUnpinnedColumn(fixedColumnCount).map { ResolvedColumn(it, it.calcWidth()) },
        fixedRowHeader = fixedRowHeader,
        onRowClick = onRowClick,
        onRowLongClick = onRowLongClick,
        //pinnedWidth = pinnedWidth,
        headerHeight = headerHeight,
        rowHeight = rowHeight,
        cellPadding = cellPadding,
        gridLines = gridLines,
        gridLineThickness = gridLineThickness,
        colors = colors,
        headerTextStyle = headerTextStyle,
        cellTextStyle = cellTextStyle,
        headerVerticalAlign = headerVerticalAlign,
        cellVerticalAlign = cellVerticalAlign,
        tableState = tableState,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScrollbar(
                state = tableState.listState,
                color = colors.scrollbarThumb,
                topInset = spec.headerHeight,
            )
//                .weight(1f)
//                .then(
//                    tableState.listState.scrollIndicatorState?.let {
//                        Modifier.nonInteractiveScrollbar(it, Orientation.Vertical)
//                    } ?: Modifier
//                ),
            .overscroll(spec.tableState.overscroll) // Общий эффект овер-скролла на всю таблицу
            .horizontalScrollShadows(
                state = tableState.horizontalScrollState,
                pinnedWidth = pinnedWidth,
                startShadowColor = colors.startShadow,
                endShadowColor = colors.endShadow,
            )
    ) {
        spec.FixedHeader()

        LazyColumn(state = tableState.listState) {
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
        val rowOverscroll = remember(tableState.overscroll) { tableState.overscroll?.withoutVisualEffect() }
        Row(modifier = Modifier
            .fillMaxWidth()
            .rowGridLine(gridLines, colors.divider, gridLineThickness)
            .horizontalScroll(state = tableState.horizontalScrollState, overscrollEffect = rowOverscroll) // Переведем овер-скролл строки на всю таблицу.
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
            .padding(cellPadding)
            .wrapContentHeight(headerVerticalAlign),
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
    val clickModifier =
        if (onRowClick != null || onRowLongClick != null) {
            Modifier.combinedClickable(
                onClick = { onRowClick?.invoke(row) },
                onLongClick = onRowLongClick?.let { { it(row) } },
            )
        } else {
            Modifier
        }

    Row(modifier = Modifier
        .height(rowHeight)
        .background(colors.rowBackground)
        .then(clickModifier)
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
        val rowOverscroll = remember(tableState.overscroll) { tableState.overscroll?.withoutVisualEffect() }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .rowGridLine(gridLines, colors.divider, gridLineThickness)
                .horizontalScroll(state = tableState.horizontalScrollState, overscrollEffect = rowOverscroll) // Переведем овер-скролл строки на всю таблицу.
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
            .padding(cellPadding)
            .wrapContentHeight(cellVerticalAlign),
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

// TODO: После выхода Material3 1.5.0 можно заменить на nonInteractiveScrollbar.
@Composable
internal fun Modifier.verticalScrollbar(
    state: ScrollableState,
    color: Color,
    topInset: Dp,
    thickness: Dp = DataTableDefaults.ScrollbarThickness,
    minThumbLength: Dp = DataTableDefaults.ScrollbarMinThumbLength,
): Modifier {
    val scrolling = state.isScrollInProgress
    val alpha by animateFloatAsState(
        targetValue = if (scrolling) 1f else 0f,
        animationSpec = if (scrolling) tween(150) else tween(300, delayMillis = 1000),
        label = "scrollbarAlpha",
    )
    val indicator = state.scrollIndicatorState ?: return this

    return drawWithContent {
        drawContent()
        val content = indicator.contentSize
        val viewport = indicator.viewportSize
        if (alpha == 0f || content <= viewport) return@drawWithContent

        val w = thickness.toPx()
        val trackTop = topInset.toPx()
        val trackHeight = size.height - trackTop
        val thumbLength = max(minThumbLength.toPx(), trackHeight * viewport / content)
        val fraction = (indicator.scrollOffset.toFloat() / (content - viewport)).coerceIn(0f, 1f)
        val top = trackTop + fraction * (trackHeight - thumbLength)

        drawRoundRect(
            color = color,
            topLeft = Offset(size.width - w, top),
            size = Size(w, thumbLength),
            cornerRadius = CornerRadius(w / 2),
            alpha = alpha,
        )
    }
}
