package ru.vik.spindel.core.designsystem.component.datatable

import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

/** Состояния прокрутки таблицы. **/
@Stable
class DataTableState internal constructor(
    internal val listState: LazyListState,
    internal val horizontalScrollState: ScrollState,
    internal val overscroll: OverscrollEffect?,
) {
    /** Индекс первой видимой строки. */
    val firstVisibleRowIndex: Int
        get() = listState.firstVisibleItemIndex

    /** Таблица прокручена вниз от начала (например, чтобы показать кнопку «Наверх»). */
    val isScrolledFromTop: Boolean
        get() = listState.canScrollBackward

    /**
     * Прокручивает таблицу к следующей строке, подходящей под условие [predicate].
     * Если дошли до конца, начинает поиск с начала (циклический переход).
     */
    suspend fun <T> animateScrollToNextMatchingRow(
        items: List<T>,
        predicate: (T) -> Boolean
    ) {
        if (items.isEmpty() || !listState.canScrollForward) return

        val currentIndex = firstVisibleRowIndex
        // Ищем подходящую строку после текущей позиции
        val nextIndex = items.withIndex()
            .firstOrNull { (index, item) -> index > currentIndex && predicate(item) }
            ?.index
        // Если ниже подходящих строк нет — ищем с самого начала таблицы
            ?: items.indexOfFirst(predicate)

        if (nextIndex != -1) {
            listState.animateScrollToItem(nextIndex)
        }
    }

    /** Прокручивает таблицу наверх с анимацией. **/
    suspend fun animateScrollToTop() {
        listState.animateScrollToItem(0)
    }

    /** Мгновенно прокручивает таблицу наверх. **/
    suspend fun scrollToTop() {
        listState.scrollToItem(0)
    }

    /** Мгновенно прокручивает таблицу к заданной строке. **/
    suspend fun scrollToRow(index: Int) {
        listState.scrollToItem(index)
    }

    /** Мгновенное прокручивает таблицу влево. **/
    suspend fun resetHorizontalScroll() {
        horizontalScrollState.scrollTo(0)
    }
}

@Composable
fun rememberDataTableState(): DataTableState {
    val listState = rememberLazyListState()
    val horizontalScrollState = rememberScrollState()
    val overscroll = rememberOverscrollEffect()
    return remember(listState, horizontalScrollState) {
        DataTableState(listState, horizontalScrollState, overscroll)
    }
}
