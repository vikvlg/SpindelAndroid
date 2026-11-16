package ru.vik.spindel.core.designsystem

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/** Представление текста в UI. **/
sealed interface UiText {
    /** Представление из обычного текста. **/
    data class DynamicString(val value: String) : UiText

    /** Представление из строкового ресурса. **/
    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : UiText

    /** Возвращает текстовое представление. **/
    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
        }
    }

    /** Возвращает текстовое представление. **/
    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }
}
