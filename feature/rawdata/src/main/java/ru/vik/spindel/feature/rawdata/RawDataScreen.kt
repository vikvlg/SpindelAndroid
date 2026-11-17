package ru.vik.spindel.feature.rawdata

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ru.vik.spindel.core.designsystem.PreviewContainer
import ru.vik.spindel.core.designsystem.ScreenPreviews
import ru.vik.spindel.core.designsystem.component.ScrollToTopButton
import ru.vik.spindel.core.designsystem.component.datatable.DataTable
import ru.vik.spindel.core.designsystem.component.datatable.DataTableColumn
import ru.vik.spindel.core.designsystem.component.datatable.DataTableState
import ru.vik.spindel.core.designsystem.component.datatable.rememberDataTableState

/** Количество строк таблицы для прокрутки, чтобы появилась кнопка "Наверх". **/
internal const val SCROLL_TO_TOP_THRESHOLD = 10

@Composable
fun RawDataRoute(viewModel: RawDataViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    RawDataScreen(state, viewModel::handleIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RawDataScreen(
    state: RawDataState,
    onIntent: (intent: RawDataIntent) -> Unit,
) {
    val tableState = rememberDataTableState()
    val columns = rememberMetricsColumns()
    var showBrewPicker by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    BrewSelectorTitle(
                        title = state.brewSelector.title.asString(),
                        subtitle = state.brewSelector.subtitle,
                        isPlaceholder = state.brewSelector.isPlaceholder,
                        expanded = showBrewPicker,
                        onClick = { showBrewPicker = true },
                    )
                },
                windowInsets = WindowInsets(), // Убираем верхний отступ
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top), // Убираем нижний отступ
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Box(Modifier.weight(1f)) {
                DataTable(
                    items = state.metrics.list,
                    key = { metric -> metric.id },
                    isLoading = state.metrics.isLoading,
                    selectedKeys = state.metrics.selected,
                    onRowClick = { metric -> onIntent(RawDataIntent.ChangeRowSelected(metric.id)) },
                    columns = columns,
                    tableState = tableState,
                    //fixedRowHeader = false,
                )
                BottomBar(
                    state = state,
                    tableState = tableState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }

    if (showBrewPicker) {
        BrewPickerSheet(
            brews = state.brewPicker.brews,
            selection = state.brewPicker.selection,
            unassignedCount = state.brewPicker.unassignedCount,
            onSelect = { onIntent(RawDataIntent.SelectBrew(it)) },
            onDismiss = { showBrewPicker = false },
        )
    }
}

/**
 * Нижняя панель экрана:
 *  - информация по выделенным строкам таблицы;
 *  - кнопка "наверх".
 */
@Composable
private fun BottomBar(
    state: RawDataState,
    tableState: DataTableState,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val showScrollToTop by remember {
        derivedStateOf { tableState.firstVisibleRowIndex > SCROLL_TO_TOP_THRESHOLD }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .height(LocalMinimumInteractiveComponentSize.current)
            .padding(horizontal = 16.dp),
    ) {
        FloatingSelectionInfo(
            visible = state.metrics.selected.isNotEmpty(),
            text = stringResource(R.string.rawdata_selection_info, state.metrics.selected.size),
            onClick = {
                // Перемещение между выделенными строками
                scope.launch {
                    tableState.animateScrollToNextMatchingRow(state.metrics.list) { metric ->
                        metric.id in state.metrics.selected
                    }
                }
            },
            modifier = Modifier.align(Alignment.Center),
        )
        ScrollToTopButton(
            visible = showScrollToTop,
            onClick = { scope.launch { tableState.animateScrollToTop() } },
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

/** Плашка с информацией о выделенных строках таблицы. **/
@Composable
private fun FloatingSelectionInfo(
    visible: Boolean,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Card {
            Text(
                text = text,
                modifier = Modifier
                    .height(40.dp)
                    .padding(horizontal = 16.dp)
                    .wrapContentHeight(Alignment.CenterVertically)
                    .clickable(onClick = onClick)
            )
        }
    }
}

/** Создает описание столбцов таблицы. **/
@Composable
private fun rememberMetricsColumns(): List<DataTableColumn<MetricUi>> {
    return remember {
        listOf(
            DataTableColumn(title = R.string.rawdata_column_id, width = DataTableColumn.Width.Fixed(36.dp)) { metric -> metric.id.toString() },
            DataTableColumn(title = R.string.rawdata_column_date, width = DataTableColumn.Width.Fixed(100.dp)) { metric -> metric.dateText },
            DataTableColumn(title = R.string.rawdata_column_gravity, width = DataTableColumn.Width.Fixed(64.dp), textAlign = TextAlign.End) { metric -> metric.gravityText },
            DataTableColumn(title = R.string.rawdata_column_temperature, width = DataTableColumn.Width.Fixed(64.dp), textAlign = TextAlign.End) { metric -> metric.temperatureText },
            DataTableColumn(title = R.string.rawdata_column_debug_text, width = DataTableColumn.Width.Flexed(200.dp)) { it.debugText },
        )
    }
}

@ScreenPreviews
@Composable
private fun RawDataContentPreview() {
    PreviewContainer {
        RawDataScreen(
            state = RawDataState(
                metrics = MetricsState(list = RawPreviewData.metricList),
            ),
            onIntent = { }
        )
    }
}

/** Отладочный текст для проверки горизонтального скролла таблицы. **/
private val MetricUi.debugText: String
    get() = if (id % 2L == 0L) "Some very long long text" else "Text"
