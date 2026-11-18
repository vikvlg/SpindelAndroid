package ru.vik.spindel.core.designsystem.component.datatable

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.vik.spindel.core.designsystem.R

enum class DataTableGridLines(
    internal val horizontal: Boolean,
    internal val vertical: Boolean,
) {
    None(horizontal = false, vertical = false),
    Horizontal(horizontal = true, vertical = false),
    Vertical(horizontal = false, vertical = true),
    All(horizontal = true, vertical = true),
}

typealias GestureCallback<T> = ((T) -> Unit)?

object DataTableDefaults {
    val HeaderHeight = 48.dp
    val RowHeight = 48.dp
    val CellPadding = PaddingValues(horizontal = 8.dp)
    val GridLines = DataTableGridLines.All
    val GridLineThickness = 1.dp
    val ScrollbarThickness = 4.dp
    val ScrollbarMinThumbLength = 24.dp
    val headerVerticalAlign = Alignment.CenterVertically
    val cellVerticalAlign = Alignment.CenterVertically

    // Внутренние размеры наружу не выставляем
    internal val StartShadowWidth = 8.dp
    internal val EndShadowWidth = 8.dp

    // Стили зависят от темы, поэтому @Composable-геттер (как ButtonDefaults.shape в Material)
    val headerTextStyle: TextStyle
        @Composable get() = MaterialTheme.typography.labelLarge

    val cellTextStyle: TextStyle
        @Composable get() = MaterialTheme.typography.bodyMedium


    const val SHADOW_ALPHA = 0.2f
    const val SCROLLBAR_ALPHA = 0.5f

    @Composable
    fun colors(
        headerBackground: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        pinnedBackground: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        rowBackground: Color = MaterialTheme.colorScheme.surface,
        selectedRowBackground: Color = MaterialTheme.colorScheme.secondaryContainer,
        headerContent: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        pinnedCellContent: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unpinnedCellContent: Color = MaterialTheme.colorScheme.onSurface,
        secondaryContent: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        divider: Color = MaterialTheme.colorScheme.outlineVariant,
        startShadow: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = SHADOW_ALPHA),
        endShadow: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = SHADOW_ALPHA),
        scrollbarThumb: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = SCROLLBAR_ALPHA),
    ): DataTableColors = DataTableColors(
        headerBackground = headerBackground,
        pinnedBackground = pinnedBackground,
        rowBackground = rowBackground,
        selectedRowBackground = selectedRowBackground,
        headerContent = headerContent,
        pinnedCellContent = pinnedCellContent,
        unpinnedCellContent = unpinnedCellContent,
        secondaryContent = secondaryContent,
        divider = divider,
        startShadow = startShadow,
        endShadow = endShadow,
        scrollbarThumb = scrollbarThumb
    )

    @Composable
    fun PlaceHolder(@StringRes strId: Int = R.string.designsystem_datatable_empty_list) {
        Text(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(Alignment.CenterVertically),
            text = stringResource(strId),
            textAlign = TextAlign.Center,
            //color = colors.secondaryContent
        )
    }
}
