package ru.vik.spindel.feature.brewing.list

import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.vik.spindel.core.brews.api.Brew

/** Экран со списком варок. */
@Composable
fun BrewListScreen(
    onChartClick: (batchId: Long) -> Unit,
    onDetailsClick: (batchId: Long) -> Unit,
    viewModel: BrewListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Сообщение об ошибке
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BrewListEffect.ShowError -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.brewList.isEmpty() && state.isFirstLoading) {
            // Если данных вообще нет и идет первая подгрузка, то покажем только значок подгрузки
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            PullToRefreshBox(
                isRefreshing = state.isLoading,
                onRefresh = { viewModel.handleIntent(BrewListIntent.RefreshBrewList) },
                state = rememberPullToRefreshState(),
                modifier = Modifier.fillMaxSize()
            ) {
                Column {
                    // Список варок
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (state.brewList.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Список варок пуст",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        } else {
                            items(
                                items = state.brewList,
                                key = { brew -> brew.id }
                            ) { brew ->
                                BrewItemRow(brew, onChartClick, onDetailsClick)
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Элемент варки. */
@Composable
fun BrewItemRow(
    brew: Brew,
    onChartClick: (brewId: Long) -> Unit,
    onDetailsClick: (brewId: Long) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var pressOffset by remember { mutableStateOf(Offset.Zero) }

    // Переводим координаты тапа в DP
    val density = LocalDensity.current
    val tapDpOffset = remember(pressOffset, density) {
        with(density) {
            DpOffset(pressOffset.x.toDp(), pressOffset.y.toDp())
        }
    }

    Box {
        // Элемент варки
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(brew.id) {
                    detectTapGestures(
                        onTap = { onChartClick(brew.id) },
                        onLongPress = { offset ->
                            pressOffset = offset
                            showMenu = true
                        }
                    )
                }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = brew.name, style = MaterialTheme.typography.titleMedium)
                Text(text = brew.date.toString(), style = MaterialTheme.typography.bodyMedium)
            }
        }

        // Всплывающее меню
        Box(
            // Смещаем Box в позицию тапа
            modifier = Modifier.offset(x = tapDpOffset.x, y = tapDpOffset.y)
        ) {
            // Общий обработчик пунктов меню
            val onMenuAction = { action: () -> Unit ->
                showMenu = false
                action()
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
            ) {
                DropdownMenuItem(
                    text = { Text("График") },
                    onClick = { onMenuAction { onChartClick(brew.id) } }
                )
                DropdownMenuItem(
                    text = { Text("Информация") },
                    onClick = { onMenuAction { onDetailsClick(brew.id) } }
                )
            }
        }
    }
}
