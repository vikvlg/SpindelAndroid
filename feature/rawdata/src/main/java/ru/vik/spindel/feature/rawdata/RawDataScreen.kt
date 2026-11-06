package ru.vik.spindel.feature.rawdata

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.vik.spindel.core.designsystem.PreviewContainer
import ru.vik.spindel.core.designsystem.ScreenPreviews
import ru.vik.spindel.core.designsystem.component.datatable.DataTable
import ru.vik.spindel.core.designsystem.component.datatable.DataTableColumn

@Composable
fun RawDataRoute(viewModel: RawDataViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    RawDataScreen(state)
}

@Composable
fun RawDataScreen(state: RawDataState) {
    Box {
        Column {
            Text(
                text = "Это таблица на ${state.list.size} строк",
            )
            DataTable(
                items = state.list,
                key = { metric -> metric.id },
                columns = listOf(
                    DataTableColumn(title = "№", width = DataTableColumn.Width.Fixed(36.dp)) { metric -> metric.id.toString() },
                    DataTableColumn(title = "Дата", width = DataTableColumn.Width.Fixed(100.dp)) { metric -> metric.date.toString() },
                    DataTableColumn(title = "Плотность", width = DataTableColumn.Width.Fixed(64.dp), textAlign = TextAlign.End) { metric -> metric.gravity.toString() },
                    DataTableColumn(title = "Температура", width = DataTableColumn.Width.Fixed(64.dp), textAlign = TextAlign.End) { metric -> metric.temperature.toString() },
                    DataTableColumn(title = "Текст", width = DataTableColumn.Width.Flexed(200.dp)) { metric -> if (metric.id % 2L == 0L) "Some very long long text" else "Text" },
                ),
                //fixedRowHeader = false,
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

Ячейки — простые Text фиксированной ширины в обычном цикле.

Жесты
На всю строку вешается combinedClickable с обработчиками нажатия и долгого нажатия.
• Нажатие
  Обычный режим: открыть карточку записи (если будет) или ничего
  Режим выделения: отметить или снять отметку
• Долгое нажатие
  Обычный режим: войти в режим и выделить строку, с вибрацией
  Режим выделения: выделить диапазон (см. ниже)

Подводные камни этой схемы
1. Эффект overscroll.
На Android 12+ при упоре в край содержимое «растягивается».
Каждый horizontalScroll рисует этот эффект сам, поэтому растянется только та строка, которую тянут, а соседние нет. Выглядит как баг.
Его нужно отключить: в новых версиях Compose у horizontalScroll есть параметр для этого, в старых — через CompositionLocal.
2. Свайп по закреплённому столбцу таблицу вбок не двигает, потому что он вне скролла.
Обычно это никому не мешает. При желании можно повесить обработку горизонтального жеста на всю строку, но это уже тонкая настройка.
3. Не делать закреплённый столбец отдельным LazyColumn. Это частая ошибка.
Тогда приходится синхронизировать два вертикальных списка, и при быстрой прокрутке появляются рассинхрон и дёрганье.
В предложенной схеме этой проблемы нет.
4. Тень у края закреплённого столбца. Её стоит показывать, когда hState.value > 0, то есть когда таблицу прокрутили вбок.
Пользователю сразу понятно, что часть столбцов ушла под край.
Состояние «прокручено или нет» лучше выводить через derivedStateOf, чтобы не перерисовывать экран на каждый пиксель прокрутки.

Состояние: DataTableState
Хранит вертикальное состояние списка и горизонтальный ScrollState.
Создаётся через rememberDataTableState(), как rememberLazyListState() в самом Compose.
Через него фича может, например, прокрутить таблицу наверх при смене фильтра.

Оформление: DataTableDefaults
Высота строки и шапки, цвета (фон шапки, выделенной строки, разделители) с разумными значениями по умолчанию, как ButtonDefaults в Material.
По умолчанию цвета берутся из вашей темы.

*/

@ScreenPreviews
@Composable
private fun RawDataContentPreview() {
    PreviewContainer {
        RawDataScreen(
            state = RawDataState(list = RawPreviewData.metricList),
        )
    }
}
