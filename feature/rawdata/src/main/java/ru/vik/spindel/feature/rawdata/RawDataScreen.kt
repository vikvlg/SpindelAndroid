package ru.vik.spindel.feature.rawdata

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlin.collections.listOf

/** Количество строк таблицы для прокрутки, чтобы появилась кнопка "Наверх". **/
internal const val SCROLL_TO_TOP_THRESHOLD = 10

@Composable
fun RawDataRoute(viewModel: RawDataViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    RawDataScreen(state, viewModel::handleIntent)
}

@Composable
fun RawDataScreen(
    state: RawDataState,
    onIntent: (intent: RawDataIntent) -> Unit,
) {
    val tableState = rememberDataTableState()
    val scope = rememberCoroutineScope()
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

    Box {
        Column {
            Text(
                text = "Это таблица на ${state.list.size} строк",
            )
            Box(Modifier.weight(1f)) {
                DataTable(
                    items = state.list,
                    key = { metric -> metric.id },
                    selectedKeys = state.selected,
                    onRowClick = { metric -> onIntent(RawDataIntent.ChangeSelected(metric.id)) },
                    columns = columns,
                    tableState = tableState,
                    //fixedRowHeader = false,
                )
                ScrollToTopButton(
                    visible = showScrollToTop,
                    onClick = { scope.launch { tableState.animateScrollToTop() } },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                )
            }
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
