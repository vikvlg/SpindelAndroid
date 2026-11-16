package ru.vik.spindel.feature.rawdata

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import ru.vik.spindel.core.designsystem.component.datatable.rememberDataTableState
import ru.vik.spindel.core.metrics.api.Metric

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
    val scope = rememberCoroutineScope()
    //val context = LocalContext.current
    val columns: List<DataTableColumn<Metric>> = remember {
        listOf(
            DataTableColumn(title = "№", width = DataTableColumn.Width.Fixed(36.dp)) { metric -> metric.id.toString() },
            DataTableColumn(title = "Дата", width = DataTableColumn.Width.Fixed(100.dp)) { metric -> metric.date.toString() },
            DataTableColumn(title = "Плотность", width = DataTableColumn.Width.Fixed(64.dp), textAlign = TextAlign.End) { metric -> metric.gravity.toString() },
            DataTableColumn(title = "Температура", width = DataTableColumn.Width.Fixed(64.dp), textAlign = TextAlign.End) { metric -> metric.temperature.toString() },
            DataTableColumn(title = "Текст", width = DataTableColumn.Width.Flexed(200.dp)) { metric -> if (metric.id % 2L == 0L) "Some very long long text" else "Text" },
        )
    }

    val showScrollToTop by remember(tableState) {
        derivedStateOf { tableState.firstVisibleRowIndex > SCROLL_TO_TOP_THRESHOLD }
    }

    var showBrewPicker by rememberSaveable { mutableStateOf(false) }
    val title: String
    val subtitle: String?
    val isPlaceholder: Boolean
    when (val selection = state.selection) {
        BrewSelection.NotSelected -> {
            title = stringResource(R.string.rawdata_brew_not_selected)
            subtitle = null
            isPlaceholder = true
        }
        BrewSelection.Unassigned -> {
            title = stringResource(R.string.rawdata_brew_unassigned)
            subtitle = null
            isPlaceholder = false
        }
        is BrewSelection.Brew -> {
            title = state.selectedBrew?.name.orEmpty()
            subtitle = state.selectedBrew?.dateText
            isPlaceholder = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    BrewSelectorTitle(
                        title = title,
                        subtitle = subtitle,
                        isPlaceholder = isPlaceholder,
                        expanded = showBrewPicker,
                        onClick = { showBrewPicker = true },
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Box(Modifier.weight(1f)) {
                DataTable(
                    items = state.list,
                    key = { metric -> metric.id },
                    isLoading = state.isMetricsLoading,
                    selectedKeys = state.selected,
                    onRowClick = { metric -> onIntent(RawDataIntent.ChangeRowSelected(metric.id)) },
                    columns = columns,
                    tableState = tableState,
                    //fixedRowHeader = false,
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .height(LocalMinimumInteractiveComponentSize.current)
                        .padding(horizontal = 16.dp),
                ) {
                    FloatingSelectionInfo(
                        visible = state.selected.isNotEmpty(),
                        text = stringResource(R.string.rawdata_selection_info, state.selected.size),
                        modifier = Modifier
                            .align(Alignment.Center),
                    )
                    ScrollToTopButton(
                        visible = showScrollToTop,
                        onClick = { scope.launch { tableState.animateScrollToTop() } },
                        modifier = Modifier
                            .align(Alignment.CenterEnd),
                    )
                }
            }
        }
    }

    if (showBrewPicker) {
        BrewPickerSheet(
            brews = state.brews,
            selection = state.selection,
            unassignedCount = state.unassignedCount,
            onSelect = { onIntent(RawDataIntent.SelectBrew(it)) },
            onDismiss = { showBrewPicker = false },
        )
    }
}

@Composable
fun FloatingSelectionInfo(
    visible: Boolean,
    text: String,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Card {
            // TODO: Добавить обработку клика и перемещение по выделенным строкам?
            Text(
                text = text,
                modifier = Modifier
                    .height(40.dp)
                    .padding(horizontal = 16.dp)
                    .wrapContentHeight(Alignment.CenterVertically)
            )
        }
    }
}

/*

Что получает компонент строки:
• UI-модель строки;
• isSelected: Boolean, а не весь набор выделенных ID;
• selectionMode: Boolean;
• hState;
• обработчики onClick(id) и onLongClick(id).

Жесты
На всю строку вешается combinedClickable с обработчиками нажатия и долгого нажатия.
• Нажатие
  Обычный режим: открыть карточку записи (если будет) или ничего
  Режим выделения: отметить или снять отметку
• Долгое нажатие
  Обычный режим: войти в режим и выделить строку, с вибрацией
  Режим выделения: выделить диапазон (см. ниже)

Подводные камни этой схемы
2. Свайп по закреплённому столбцу таблицу вбок не двигает, потому что он вне скролла.
Обычно это никому не мешает. При желании можно повесить обработку горизонтального жеста на всю строку, но это уже тонкая настройка.

*/

@ScreenPreviews
@Composable
private fun RawDataContentPreview() {
    PreviewContainer {
        RawDataScreen(
            state = RawDataState(list = RawPreviewData.metricList),
            onIntent = { }
        )
    }
}
