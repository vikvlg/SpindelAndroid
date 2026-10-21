package ru.vik.spindel.feature.brewing.list

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.vik.spindel.core.brews.api.Brew

@Composable
fun BrewListScreen(
    onSubItemClick: (batchId: Long) -> Unit,
    viewModel: BrewListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BrewListEffect.ShowError -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.handleIntent(BrewListIntent.RefreshClicked)
    }

    // Отрисовка UI
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.brewList.isEmpty() && state.isLoading) {
            // Показываем полный экран загрузки, если в БД еще ничего нет, но сеть идет
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            // Если в БД что-то есть, оно отобразится мгновенно!
            // А если параллельно идет загрузка, сверху списка можно показать, например, LinearProgressIndicator
            Column {
                if (state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.brewList,
                        key = { brew -> brew.id }
                    ) { brew ->
                        BrewItemRow(brew, onSubItemClick)
                    }
                }
            }
        }
    }
}

@Composable
fun BrewItemRow(
    brew: Brew,
    onItemClick: (brewId: Long) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(brew.id) },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = brew.name, style = MaterialTheme.typography.titleMedium)
            Text(text = brew.date.toString(), style = MaterialTheme.typography.bodyMedium)
        }
    }
}