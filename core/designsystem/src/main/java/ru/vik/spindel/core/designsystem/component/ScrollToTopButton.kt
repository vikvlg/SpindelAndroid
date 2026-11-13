package ru.vik.spindel.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.vik.spindel.core.designsystem.R

/** Кнопка "Наверх". **/
@Composable
fun ScrollToTopButton(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
    ) {
        SmallFloatingActionButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
//                painter = painterResource(R.drawable.ic_arrow_upward),
                contentDescription = stringResource(R.string.designsystem_scroll_to_top),
            )
        }
    }
}
