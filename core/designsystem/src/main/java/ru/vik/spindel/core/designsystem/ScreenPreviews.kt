package ru.vik.spindel.core.designsystem

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.vik.spindel.core.designsystem.theme.SpindelTheme

/** Список распространенных предпросмотров экранов. **/
@Preview(name = "Светлая", showBackground = true)
@Preview(name = "Тёмная", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Крупный шрифт", showBackground = true, fontScale = 1.5f)
annotation class ScreenPreviews

/** Контейнер для предпросмотра экрана. **/
@Composable
fun PreviewContainer(content: @Composable () -> Unit) {
    SpindelTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}