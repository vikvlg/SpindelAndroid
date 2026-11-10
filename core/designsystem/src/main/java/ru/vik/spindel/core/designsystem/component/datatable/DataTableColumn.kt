package ru.vik.spindel.core.designsystem.component.datatable

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp

/** Данные столбца таблицы. **/
@Immutable
data class DataTableColumn<T>(
    /** Заголовок столбца. **/
    val title: String,
    /** Выравнивание текста заголовка столбца. **/
    val titleAlign: TextAlign? = TextAlign.Center,
    /** Ширина столбца. **/
    val width: Width,
    /** Выравнивание текста внутри ячейки. **/
    val textAlign: TextAlign? = null,
    //val textAlign: Alignment.Horizontal = Alignment.Start,
    /** Текст ячейки. **/
    val text: (T) -> String
) {
    /** Ширина столбца. **/
    sealed interface Width {
        /** Фиксированная ширина столбца. **/
        data class Fixed(val fixedWidth: Dp) : Width
        /** Плавающая ширина столбца. **/
        data class Flexed(val minWidth: Dp, val weight: Float = 1f) : Width
    }
}