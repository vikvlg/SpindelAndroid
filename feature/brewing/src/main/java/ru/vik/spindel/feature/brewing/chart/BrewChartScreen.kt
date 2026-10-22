package ru.vik.spindel.feature.brewing.chart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun BrewChartScreen(
    batchId: Long,
    viewModel: BrewChartViewModel = hiltViewModel()
) {
    val metricsState by viewModel.metrics.collectAsStateWithLifecycle()
    val modelProducer = remember { CartesianChartModelProducer() }

    // Форматирование подписи X-оси
    val labelListKey = ExtraStore.Key<List<LocalDateTime>>() // Ключ для хранения списка дат внутри модели графика
    val formatter = DateTimeFormatter.ofPattern("dd.MM HH:mm")
    val bottomAxisValueFormatter = CartesianValueFormatter { context, x, _ ->
        val dates = context.model.extraStore[labelListKey]
        val index = x.toInt()
        if (index in dates.indices) {
            dates[index].format(formatter)
        } else {
            "-"
        }
    }

    // Реагируем на изменение списка метрик
    LaunchedEffect(metricsState) {
        if (metricsState.isNotEmpty()) {
            modelProducer.runTransaction {
                lineModel {
                    series(
                        //x = metricsState.map { it.id },
                        y = metricsState.map { it.gravity }
                    )
                }
                // Записываем даты в ExtraStore под нашим ключом
                extras { it[labelListKey] = metricsState.map { metric -> metric.date } }
            }
        }
    }

    Column {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = bottomAxisValueFormatter,
                ),
            ),
            modelProducer = modelProducer,
            zoomState = rememberVicoZoomState(zoomEnabled = false, initialZoom = Zoom.Content),
            modifier = Modifier.fillMaxSize(),
            placeholder = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading chart for batch $batchId...")
                }
            }
        )
    }
}
