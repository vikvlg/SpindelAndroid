package ru.vik.spindel.feature.brewing.chart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.axis.Axis
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.Fill
import ru.vik.spindel.core.designsystem.toUiString
import ru.vik.spindel.feature.brewing.R

// Экземпляры создаются один раз: Vico просит не подменять rangeProvider у слоя
private val GravityRangeProvider = WholeDaysRangeProvider(CartesianLayerRangeProvider.auto())
private val TemperatureRangeProvider = WholeDaysRangeProvider(PaddedYRange(padding = 4.0))

@Composable
fun BrewChartScreen(
    viewModel: BrewChartViewModel = hiltViewModel()
) {
    val metricsState by viewModel.metrics.collectAsStateWithLifecycle()
    val modelProducer = remember { CartesianChartModelProducer() }
    val axisGravityUnit = stringResource(R.string.brewing_chart_axis_gravity_unit)
    val axisTemperatureUnit = stringResource(R.string.brewing_chart_axis_temperature_unit)

    LaunchedEffect(metricsState) {
        if (metricsState.isEmpty()) return@LaunchedEffect
        val x = metricsState.map { it.date.toChartX() }
        modelProducer.runTransaction {
            // Порядок важен: первая модель достаётся первому линейному слою, вторая — второму
            lineModel { series(x = x, y = metricsState.map { it.gravity }) }
            lineModel { series(x = x, y = metricsState.map { it.temperature }) }
        }
    }

    val gravityColor = MaterialTheme.colorScheme.primary
    val temperatureColor = MaterialTheme.colorScheme.tertiary
    val gravityLayer = rememberLineCartesianLayer(
        lineProvider = LineCartesianLayer.LineProvider.series(
            LineCartesianLayer.rememberLine(
                fill = remember(gravityColor) { LineCartesianLayer.LineFill.single(Fill(gravityColor)) },
            ),
        ),
        rangeProvider = GravityRangeProvider,
        verticalAxisPosition = Axis.Position.Vertical.End, // шкала справа
    )
    val temperatureLayer = rememberLineCartesianLayer(
        lineProvider = LineCartesianLayer.LineProvider.series(
            LineCartesianLayer.rememberLine(
                fill = remember(temperatureColor) { LineCartesianLayer.LineFill.single(Fill(temperatureColor)) },
            ),
        ),
        rangeProvider = TemperatureRangeProvider,
        verticalAxisPosition = Axis.Position.Vertical.Start, // шкала слева
    )

    val bottomAxisValueFormatter = remember {
        CartesianValueFormatter { _, x, _ -> x.toChartDate().toUiString() }
    }
    val gravityAxisValueFormatter = remember {
        CartesianValueFormatter.decimal(decimalCount = 1, suffix = axisGravityUnit)
    }
    val temperatureAxisValueFormatter = remember {
        CartesianValueFormatter.decimal(decimalCount = 0, suffix = axisTemperatureUnit)
    }

    Column {
        CartesianChartHost(
            chart = rememberCartesianChart(
                gravityLayer, temperatureLayer,
                startAxis = VerticalAxis.rememberStart(
                    valueFormatter = temperatureAxisValueFormatter,
                    guideline = null, // сетку рисует только шкала плотности
                ),
                endAxis = VerticalAxis.rememberEnd(
                    valueFormatter = gravityAxisValueFormatter,
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = bottomAxisValueFormatter,
                ),
                getXStep = { _, _, _ -> 1.0 }, // шаг оси X — одни сутки
            ),
            modelProducer = modelProducer,
            zoomState = rememberVicoZoomState(zoomEnabled = false, initialZoom = Zoom.Content),
            modifier = Modifier.fillMaxSize(),
            placeholder = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.brewing_chart_loading))
                }
            }
        )
    }
}
